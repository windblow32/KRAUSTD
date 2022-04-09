package main.java.Embedding.EMBDI.SourceEmbedding;

import com.google.common.collect.Lists;
import com.medallia.word2vec.NormalizedWord2VecModel;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;
import com.medallia.word2vec.thrift.Word2VecModelThrift;
import com.medallia.word2vec.util.Format;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SourceEmbeddingViaWord2Vec{
    public List<String> total_nodes = new ArrayList<>();
    public Word2VecModel word2VecModel;
    public List<List<String>> smallList = new ArrayList<>();

    public List<Double> train(List<String> fileList, String graphFilePath, int n_walks, int n_nodes, int length) {
        MetaAlgorithm meta = new MetaAlgorithm();
        // todo : judge whether graph is saved
        String testFilePath = graphFilePath;
        File testFile = new File(testFilePath);
        List<String> data = null;
        // fixme 图的名字没有更改，一旦存储了一次就不被更新
        if(testFile.exists()){
            // 图已经建立，其他数值无法改变了
            data = meta.Meta_AlgorithmUseGraphFilePath(graphFilePath, n_walks, n_nodes, length);
        }
        else {
            // 不存在就训练
            data = meta.Meta_Algorithm(fileList, n_walks, n_nodes, length);
        }
        // fixme : disable total_nodes to test heap
        this.total_nodes.addAll(meta.nodes);
        // save node
        String totalNodePath = "data/stock100/totalMinNode2.txt";
        saveList(totalNodePath, total_nodes);
        System.out.println("save totalNodes successfully");

        List<String> finalData = data;
        List<List<String>> list = data.stream().map(var11 -> finalData).collect(Collectors.toList());

        List<String> temp = new ArrayList<>();
        Iterator<List<String>> itor = list.iterator();
        int k = 0;
        temp = itor.next();
        System.out.println("list size : "+temp.size());
        int index = 0;
        // useNum instead of temp.size()
        for (k = 0; k < temp.size(); k++) {
            // fixme: sublist 是视图，不能本地化
            List<String> tempList = new ArrayList<>();
            for (int t = index; t < index + length; t++) {
                if(t<temp.size()){
                    tempList.add(temp.get(t));
                }
            }
            smallList.add(tempList);
            index += length;
        }

        System.out.println("train successfully");
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

    public Map<String,List<Double>> getEmbeddings() throws Searcher.UnknownWordException {
//        nodes.add("1");
        Map<String,List<Double>> vecMap = new HashMap<>();
        List<String> nodeslist = new ArrayList<>(total_nodes);
        for(String word : nodeslist){
            List<Double> list = new ArrayList<>();
            Searcher searcher = word2VecModel.forSearch();
            list.addAll(searcher.getRawVector(word));
            vecMap.put(word, list);
        }
        return vecMap;
    }


    private boolean judge(String str){
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
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
    public Word2VecModel trainWithLocalWalks(String modelSavePath) {
        try {
            word2VecModel = Word2VecModel.trainer().
                    setMinVocabFrequency(1).useNumThreads(2).setWindowSize(1).
                    type(NeuralNetworkType.CBOW).setLayerSize(50).useHierarchicalSoftmax().
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

    public ArrayList<float[]> getSampleEmbedding(Word2VecModel model) {
        ArrayList<float[]> result = new ArrayList<>();
        int sampleNum = 100;
        Searcher search = model.forSearch();
        // row_0 to row_99
        for(int i = 0;i<sampleNum;i++){
            String str = "row_" + i;
            try {
                List<Double> sampleEMBDI = search.getRawVector(str);
                // get embedding size
                int size = sampleEMBDI.size();

                float[] sampleArray = new float[size];
                int index = 0;
                for(double vei : sampleEMBDI){
                    sampleArray[index] = (float)vei;
                }
                result.add(sampleArray);

            } catch (Searcher.UnknownWordException e) {
                e.printStackTrace();
                System.out.println("not find row_" + i);
            }
        }
        return result;

    }
}

