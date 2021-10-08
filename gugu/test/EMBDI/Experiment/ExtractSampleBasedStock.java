package EMBDI.Experiment;

import EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * using TripartiteGraph to generate random EMSize nodes, and calc their distance
 * log in E:\GitHub\ICDE2021\gugu\log\newStock
 * data in data/generateSample/dividedSource
 */
public class ExtractSampleBasedStock {
    public int sourceNum = 15;
    public int tupleNum = 10;
    @Test
    public void train() {
        SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();

        // 先输入两个数据集
        for(int i = 0;i<sourceNum;i++){
            int temp = i + 1;
            String filePath = "data/generateSample/dividedSource/source" + temp + ".csv";
            fileList.add(filePath);
        }

        // todo:add truth, default the last one
        String truthPath = "data/generateSample/truth.csv";
        fileList.add(truthPath);

        // time located between both sides of code!
        long preTrainMemory = used();
        long preTrainTime = System.currentTimeMillis();
        List<Double> vector = word2VecService.train(fileList,20,3,3);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();

        // 尝试分批次训练,现训练两个数据集然后保存，得到模型pretest1
        String modelPath = "model/Tri/SampleStockTest/total.model";
        word2VecService.saveModel(modelPath);
        System.out.println(vector.size());

        // 再构造文件列表，再加入几个
//        List<String> newFileList = new ArrayList<>();
//        for(int i = 2;i<4;i++){
//            int temp = i + 1;
//            String filePath = "data/generateSample/dividedSource/source" + temp + ".csv";
//            newFileList.add(filePath);
//        }
        // 用的是同一个service



        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 6;
        Map<String,List<Double>> K_map = new HashMap<>();
        try {
            long preGetRandomMemory = used();
            long preGetTime = System.currentTimeMillis();
            K_map = word2VecService.getRandom_K_Embeddings(EMSize);
            long afterGetTime = System.currentTimeMillis();
            long afterGetRandomMemory = used();

            int realSize = K_map.size();

            // word2vec embedding time
            long embeddingTime = afterGetTime - preGetTime;
            // word2vec embedding memory
            long embeddingCalcMemory = afterGetRandomMemory - preGetRandomMemory;
            // System.out.println(word2VecService.getEmbeddings());
            File f=new File("log/ExtractStock/ExtractSampleTri1.txt");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String,List<Double>>> entrySet = K_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while(it2.hasNext()){
                Map.Entry<String,List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID+" "+stu);
            }
            System.out.println("model training memory is : " + trainMemory);
            System.out.println("model training time is : " + trainTime);
            System.out.println("random " + realSize + " embeddings calc memory is : " + embeddingCalcMemory);
            System.out.println("random " + realSize + " embeddings calc time is : " + embeddingTime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // node in K_map : calc distance between each of 2

        Set<String> nameSet = new HashSet<>(K_map.keySet());
        Iterator<String> nameItor = nameSet.iterator();
        List<String> nameList = new ArrayList<>();
        while(nameItor.hasNext()){
            String name = nameItor.next();
            nameList.add(name);
        }
        int nameListSize = nameList.size();
        String v1;
        String v2=null;
        for(int i = 0;i<nameListSize;i++){
            v1 = nameList.get(i);
            for(int j = i + 1;j<nameListSize;j++){
                v2 = nameList.get(j);
                double d = word2VecService.distance(v1,v2);
                System.out.println(v1 + " 和 " + v2 + " 间的相似度为 : " + d);
            }

        }




    }

    /**
     *
     * @return memory cost current
     */
    public static long used(){
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        return total - free;
    }
//    long start = System.currentTimeMillis();
}

