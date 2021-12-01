package EMBDI.SourceEmbedding;

import EMBDI.Meta_Algorithm_for_EMBDI;
import com.google.common.collect.Lists;
import com.medallia.word2vec.NormalizedWord2VecModel;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;
import com.medallia.word2vec.thrift.Word2VecModelThrift;
import com.medallia.word2vec.util.Format;
import lombok.extern.slf4j.Slf4j;


import java.util.*;
import java.util.regex.Pattern;


@Slf4j
public class SourceEmbeddingViaWord2Vec{
    public List<String> total_nodes = new ArrayList<>();
    public Word2VecModel word2VecModel;
    public List<Double> train(List<String> fileList, int n_walks, int n_nodes, int length) {
        try {
            MetaAlgorithm meta = new MetaAlgorithm();
            List data = meta.Meta_Algorithm(fileList, n_walks, n_nodes, length);
            this.total_nodes.addAll(meta.nodes);
            List list =  Lists.transform(data, var11 -> data);
            // todo : parameter should be cared
            word2VecModel = Word2VecModel.trainer().
                    setMinVocabFrequency(1).useNumThreads(4).setWindowSize(1).
                    type(NeuralNetworkType.CBOW).setLayerSize(10).useHierarchicalSoftmax().
                    useNegativeSamples(5).setDownSamplingRate(1.0E-4D).
                    setNumIterations(5).setListener((var1, var2) -> System.out.println(String.format("%s is %.2f%% complete", Format.formatEnum(var1), var2 * 100.0D))).train(list);
            Word2VecModelThrift thrift = word2VecModel.toThrift();
            NormalizedWord2VecModel.fromThrift(thrift);
            return new ArrayList<Double>(thrift.getVectors());

        } catch (InterruptedException e) {
            log.error("exception:{}", e);

            return null;

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

    /**
     *
     * @param k : get k nodes' embeddings
     * @return a map contains k nodes' embeddings
     */
    public Map<String, List<Double>> getRandom_K_Embeddings(int k){

        Map<String, List<Double>> vecMap = new HashMap<>();
        List<String> nodeList = new ArrayList<>(total_nodes);
        Set<Integer> indexSet = new HashSet<>();
        // 随机获取list下标
        while(indexSet.size()<k){
            int index = (int) (Math.random()*nodeList.size());
            if(indexSet.contains(index)){
                continue;
            }
            // use index to get word
            String word = nodeList.get(index);
            List<Double> list = new ArrayList<>();
            Searcher searcher = word2VecModel.forSearch();
            try {
                list.addAll(searcher.getRawVector(word));
            } catch (Searcher.UnknownWordException e) {
                e.printStackTrace();
            }
            vecMap.put(word, list);
            indexSet.add(index);
        }
        if (vecMap.size()!=k){
            System.out.println("embedding 不足K个");
        }
        return vecMap;
    }


    /**
     * 计算两个词之间的距离,注意，不能是属性啊！
     * todo:CSY需要的是什么之间的
     * @param s1 word1
     * @param s2 word2
     * @return double distance
     */
    public double distance(String s1, String s2){
//        if(!(judge(s1)&&judge(s2))){
//            System.out.println("待比较的不是double类型");
//        }
        Searcher search = word2VecModel.forSearch();
        double d = 0;
        try {
            d = search.cosineDistance(s1,s2);
        } catch (Searcher.UnknownWordException e) {
            e.printStackTrace();
        }
        return d;
    }
    private boolean judge(String str){
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
    }
}

