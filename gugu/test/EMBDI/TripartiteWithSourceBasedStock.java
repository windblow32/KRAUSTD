package EMBDI;

import EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * using TripartiteGraph to generate random EMSize nodes, and calc their distance
 * log in E:\GitHub\ICDE2021\gugu\log\newStock
 * data in gugu/data/newstock/smallStock*.csv
 */
public class TripartiteWithSourceBasedStock {
    @Test
    public void train() {
        SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();
        fileList.add("data/newstock/smallStock2.CSV");
        fileList.add("data/newstock/smallStock3.CSV");
//        fileList.add("data/newstock/smallStock4.CSV");
//        fileList.add("data/newstock/smallStock16.CSV");

        // todo:add truth, default the last one

        // time located between both sides of code!
        long preTrainMemory = used();
        long preTrainTime = System.currentTimeMillis();
        List<Double> vector = word2VecService.train(fileList,20,3,3,10);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();

        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 5;
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
            File f=new File("log/newStock/SourceThreeEMBDI7.txt");
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
