package main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph;

import com.medallia.word2vec.NormalizedWord2VecModel;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;
import com.medallia.word2vec.thrift.Word2VecModelThrift;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.medallia.word2vec.Word2VecModel.fromBinFile;

@Slf4j
public class NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec {
    public List<String> total_nodes = new ArrayList<>();
    public Word2VecModel word2VecModel;
    public List<List<String>> smallList = new ArrayList<>();

    public List<Double> train(List<String> fileList, String graphFilePath, int n_walks, int n_nodes, int length, int useNum, int AttrDistributeLow,
                              int AttrDistributeHigh,
                              int ValueDistributeLow,
                              int ValueDistributeHigh,
                              int TupleDistributeLow,
                              int TupleDistributeHigh) {
        try {
            MetaAlgorithmNormalizeDistribute meta = new MetaAlgorithmNormalizeDistribute();
//            List data = meta.Meta_Algorithm(fileList, n_walks, n_nodes, length);
//            this.total_nodes.addAll(meta.nodes);
//            List list = Lists.transform(data, var11 -> data);
            // todo : judge whether graph is saved
            String testFilePath = graphFilePath;
            File testFile = new File(testFilePath);
            List<String> data = null;
            if(testFile.exists()){
                data = meta.Meta_AlgorithmUseGraphFilePath(graphFilePath, n_walks, n_nodes, length);
            }
            else {
                // 不存在就训练
                data = meta.Meta_Algorithm(fileList, n_walks, n_nodes, length, AttrDistributeLow,
                        AttrDistributeHigh,
                        ValueDistributeLow,
                        ValueDistributeHigh,
                        TupleDistributeLow,
                        TupleDistributeHigh);
            }

            // fixme : disable total_nodes to test heap
            this.total_nodes.addAll(meta.nodes);
            // save node
            String totalNodePath = "data/stock100/totalMinNode1.txt";
            saveList(totalNodePath, total_nodes);
            System.out.println("save totalNodes successfully");

            List<String> finalData = data;
            List<List<String>> list = data.stream().map(var11 -> finalData).collect(Collectors.toList());

            List<String> temp = new ArrayList<>();
            Iterator<List<String>> itor = list.iterator();
            int k = 0;
            temp = itor.next();
            System.out.println("list size : "+temp.size());
            System.out.println("use : "+useNum);
            int index = 0;
            for (k = 0; k < useNum; k++) {
                // fixme: sublist 是视图，不能本地化
                List<String> tempList = new ArrayList<>();
                for (int t = index; t < index + length; t++) {
                    tempList.add(temp.get(t));
                }
                smallList.add(tempList);
                index += length;
            }

            System.out.println("train successfully");

        } catch (InterruptedException e) {
            log.error("exception:{0}", e);

            return null;
        }
        return null;
    }

    /**
     * @param graphFilePath store the path of graph
     * @param n_walks       n_walks/n_nodes * totalNodes = sum of walks(according to meta)
     * @param n_nodes
     * @param length        walk length
     * @return embedding list
     */
    public void trainWithGraphPath(String graphFilePath, String smallListPath, int n_walks, int n_nodes, int length, int useNum) {
        try {
            MetaAlgorithmNormalizeDistribute meta = new MetaAlgorithmNormalizeDistribute();
//            List data = meta.Meta_Algorithm(fileList, n_walks, n_nodes, length);
//            this.total_nodes.addAll(meta.nodes);
//            List list = Lists.transform(data, var11 -> data);
            List<String> data = meta.Meta_AlgorithmUseGraphFilePath(graphFilePath, n_walks, n_nodes, length);
            // fixme : disable total_nodes to test heap
            this.total_nodes.addAll(meta.nodes);
            // save node, 测试用，后面没用
            String totalNodePath = "data/stock100/totalNode10.txt";
            saveList(totalNodePath, total_nodes);
            System.out.println("save totalNodes successfully");
            List<List<String>> list = data.stream().map(var11 -> data).collect(Collectors.toList());

            List<String> temp = new ArrayList<>();
            Iterator<List<String>> itor = list.iterator();
            System.out.println(list.size());
            int k = 0;
            temp = itor.next();
            int index = 0;
            for (k = 0; k < useNum; k++) {
                // fixme: sublist 是视图，不能本地化
                List<String> tempList = new ArrayList<>();
                for (int t = index; t < index + length; t++) {
                    tempList.add(temp.get(t));
                }
                smallList.add(tempList);
                index += length;
            }

//            while(itor.hasNext()){
//                temp = itor.next();
//                for(String str:temp){
//                    // 有效数据,有数据源或者tuple
//                    if(judgeSource(str)||judgeTuple(str)){
//                        // fixme:smallList has num_of_node*length(20) as its length, not our
//                        smallList.add(temp);
//                        i++;
//                        break;
//                    }
//                }
//                // use all nodes
//                if(i==useNum){
//                    break;
//                }
//            }
            System.out.println("used " + k);
            System.out.println("smallList size " + smallList.size());
            //  smallList save in file
            // fixme
            // saveListOfList(smallListPath,smallList);

            Thread.sleep(200);
            System.out.println("train successfully");

        } catch (InterruptedException e) {
            log.error("exception:{0}", e);
        }
    }

    /**
     * @param modelFilePath 模型保存位置
     */
    public void saveModel(Word2VecModel model, String modelFilePath) {
        File f = new File(modelFilePath);
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fo);
            System.setOut(printStream);
            model.toBinFile(printStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Word2VecModel loadModel(String modelFilePath) {
        File f = new File(modelFilePath);
        try {
            return fromBinFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 出问题
        System.out.println("model didn't load successfully");
        return null;
    }

    public Map<String, List<Double>> getEmbeddings() throws Searcher.UnknownWordException {
//        nodes.add("1");
        Map<String, List<Double>> vecMap = new HashMap<>();
        List<String> nodeslist = new ArrayList<>(total_nodes);
        for (String word : nodeslist) {
            Searcher searcher = word2VecModel.forSearch();
            List<Double> list = new ArrayList<>(searcher.getRawVector(word));
            vecMap.put(word, list);
        }
        return vecMap;
    }

    /**
     * @param k : get k nodes' embeddings
     * @return a map contains k nodes' embeddings
     */
    public Map<String, List<Double>> getRandom_K_Embeddings(int k) {

        Map<String, List<Double>> vecMap = new HashMap<>();

//        List<String> nodeListTemp = new ArrayList<>(total_nodes);
        List<String> nodeList = new ArrayList<>();
//        // 进行正则匹配，只保留符合row_i的，也就是样本点
//        nodeList = nodeListTemp.stream().filter(this::judgeTuple).collect(Collectors.toList());
        Iterator<List<String>> itor = smallList.iterator();
        List<String> temp = new ArrayList<>();
        while (itor.hasNext()) {
            temp = itor.next();
            nodeList.addAll(temp.stream().filter(this::judgeTuple).collect(Collectors.toList()));
        }
        Set<Integer> indexSet = new HashSet<>();
        // 随机获取list下标
        while (indexSet.size() < k) {
            // 随机选出的路径应该包含需要比较的元组
            int index = (int) (Math.random() * nodeList.size());
            if (indexSet.contains(index)) {
                continue;
            }
            // use index to get word
            String word = nodeList.get(index);
            List<Double> list = new ArrayList<>();
            // fixme : use load model
            Searcher searcher = word2VecModel.forSearch();
            try {
                list.addAll(searcher.getRawVector(word));
            } catch (Searcher.UnknownWordException e) {
                e.printStackTrace();
            }
            vecMap.put(word, list);
            indexSet.add(index);
        }
        if (vecMap.size() != k) {
            System.out.println("embedding 不足K个");
        }
        return vecMap;
    }

    /**
     * without model path, find some error in the other version
     *
     * @return
     */
    public Map<String, List<Double>> get_inner_Source_EMBDI() {

        Searcher searcher = word2VecModel.forSearch();
        Map<String, List<Double>> vecMap = new HashMap<>();
        for (int i = 1; i <= 55; i++) {
            String word = "source_" + i;
            try {
                List<Double> list = new ArrayList<>(searcher.getRawVector(word));
                vecMap.put(word, list);
            } catch (Searcher.UnknownWordException e) {
                e.printStackTrace();
            }

        }
        return vecMap;
    }

    /**
     * @param modelPath saved model path
     * @return KV
     * @throws Searcher.UnknownWordException
     */
    public Map<String, List<Double>> getRandom_Source_Embeddings(String modelPath) throws Searcher.UnknownWordException {
        // fixme : use load model
        Word2VecModel model = loadModel(modelPath);
        Searcher searcher = model.forSearch();
        Map<String, List<Double>> vecMap = new HashMap<>();
        for (int i = 1; i <= 55 + 1; i++) {
            String word = "source_" + i;
            try {
                List<Double> list = new ArrayList<>(searcher.getRawVector(word));
                vecMap.put(word, list);
            } catch (Searcher.UnknownWordException e) {
                e.printStackTrace();
            }

        }
        return vecMap;
    }

    /**
     * 计算两个词之间的距离,注意，不能是属性啊！
     * todo:CSY需要的是什么之间的
     *
     * @param s1 word1
     * @param s2 word2
     * @return double distance
     */
    public double distance(String s1, String s2) {
//        if(!(judge(s1)&&judge(s2))){
//            System.out.println("待比较的不是double类型");
//        }
        Searcher search = word2VecModel.forSearch();
        double d = 0;
        try {
            d = search.cosineDistance(s1, s2);
            List<Double> s1List = search.getRawVector(s1);
            List<Double> s2List = search.getRawVector(s2);
            double total1 = 0;
            for (double s : s1List) {
                total1 += s * s;
            }
            double model1 = Math.sqrt(total1);
            double total2 = 0;
            for (double s : s2List) {
                total2 += s * s;
            }
            double model2 = Math.sqrt(total2);
            return d / (model1 * model2);
        } catch (Searcher.UnknownWordException e) {
//            e.printStackTrace();
            return 0;
        }
    }

    private boolean judge(String str) {
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
    }

    /**
     * @param str:nodeName in graph
     * @return whether node is AKO tuple
     */
    private boolean judgeTuple(String str) {
        String regex = "row_([0-9]+)";
        return Pattern.matches(regex, str);
    }

    /**
     * fixme: truth file is the last file (56) in fileList, didn't separate
     *
     * @param str nodeName in graph
     * @return whether node is AKO source
     */
    private boolean judgeSource(String str) {
        String regex = "source_([0-9]+)";
        return Pattern.matches(regex, str);
    }

    public List<String> readListFromFile(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<String> result = (List<String>) objectInputStream.readObject();
            fileInputStream.close();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("ListFile not found!");
        return null;
    }

    public List<List<String>> readListOfListFromFile(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<List<String>> result = (List<List<String>>) objectInputStream.readObject();
            fileInputStream.close();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("ListFile not found!");
        return null;
    }

    public void saveList(String filePath, List<String> nodeList) {
        File f = new File(filePath);
        try {
            f.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(f);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(nodeList);
            outputStream.close();
            System.out.println("List saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveListOfList(String filePath, List<List<String>> List) {
        File f = new File(filePath);
        try {
            f.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(f);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(List);
            outputStream.close();
            System.out.println("List of List saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * use list to get embedding
     *
     * @param list list stored by saveListOfList
     * @return return list of embedding
     */
    public Word2VecModel trainWithWalks(List<List<String>> list, String modelSavePath) {
        try {
            word2VecModel = Word2VecModel.trainer().
                    setMinVocabFrequency(1).useNumThreads(2).setWindowSize(1).
                    type(NeuralNetworkType.CBOW).setLayerSize(10).useHierarchicalSoftmax().
                    useNegativeSamples(5).setDownSamplingRate(1.0E-2D).
                    setNumIterations(5).train(list);
            Word2VecModelThrift thrift = word2VecModel.toThrift();
            NormalizedWord2VecModel.fromThrift(thrift);
            saveModel(word2VecModel, modelSavePath);
            return word2VecModel;
//            return new ArrayList<Double>(thrift.getVectors());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("trainWithWalks Error");
        return null;
    }

    public Word2VecModel trainWithLocalWalks(String modelSavePath) {
        try {
            word2VecModel = Word2VecModel.trainer().
                    setMinVocabFrequency(1).useNumThreads(2).setWindowSize(1).
                    type(NeuralNetworkType.CBOW).setLayerSize(10).useHierarchicalSoftmax().
                    useNegativeSamples(5).setDownSamplingRate(1.0E-2D).
                    setNumIterations(5).train(smallList);
            Word2VecModelThrift thrift = word2VecModel.toThrift();
            NormalizedWord2VecModel.fromThrift(thrift);
            saveModel(word2VecModel, modelSavePath);
            return word2VecModel;
//            return new ArrayList<Double>(thrift.getVectors());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("trainWithWalks Error");
        return null;
    }
    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2){
        Searcher search = model.forSearch();
        double d = 0;
        try {
            d = search.cosineDistance(s1, s2);
            List<Double> s1List = search.getRawVector(s1);
            List<Double> s2List = search.getRawVector(s2);
            double total1 = 0;
            for(double s:s1List){
                total1 += s*s;
            }
            double model1 = Math.sqrt(total1);
            double total2 = 0;
            for(double s : s2List){
                total2 += s*s;
            }
            double model2 = Math.sqrt(total2);
            return d/(model1*model2);
        } catch (Searcher.UnknownWordException e) {
//            e.printStackTrace();
            return 0;
        }

    }

}

