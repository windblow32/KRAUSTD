package EMBDI.TripartiteWeightedGraphWithSource;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;

public class SampledTripartiteEmbeddingViaWord2VecTest {

    public int sourceNum = 55;
    public int tupleNum = 10;

    @Test
    public void train() {
        SampledTripartiteEmbeddingViaWord2Vec word2VecService = new SampledTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();

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
        List<Double> vector = word2VecService.train(fileList,40,3,5);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();

        String modelPath = "model/Tri/SampleStockTest/sample.model";
        word2VecService.saveModel(modelPath);

        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 9;
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
            File f=new File("log/ExtractStock/ExtractSampleTri3.txt");
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

}