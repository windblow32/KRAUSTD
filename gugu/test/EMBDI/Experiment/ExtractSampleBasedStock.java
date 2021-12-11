package EMBDI.Experiment;

import EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * using TripartiteGraph to generate random EMSize nodes, and calc their distance
 * log in E:\GitHub\ICDE2021\gugu\log\newStock
 * data in data/generateSample/dividedSource
 */
public class ExtractSampleBasedStock {
    public int sourceNum = 55;
    public int tupleNum = 100;
    // file name
    public int version = 13;
    @Test
    public void train() {
            SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
            List<String> fileList = new ArrayList<>();

            for(int i = 0;i<sourceNum;i++){
                int temp = i + 1;
                String filePath = "data/stock100/divideSource/source" + temp + ".csv";
                fileList.add(filePath);
            }

            // todo:add truth, default the last one
            String truthPath = "data/stock100/100truth.csv";
        fileList.add(truthPath);

        // time located between both sides of code!
        long preTrainMemory = used();
        long preTrainTime = System.currentTimeMillis();
        // List<Double> vector = word2VecService.train(fileList,3,3,5);
        word2VecService.train(fileList,3,3,25,20000);
        // fixme: must same as the path in train method !!!
        String walkPath = "data/stock100/walkList10.txt";
        String modelPath = "model/Tri/stock100/total10.model";
        List<Double> vector = word2VecService.trainWithWalks(word2VecService.readListOfListFromFile(walkPath),modelPath);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();

        System.out.println(vector.size());



        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 100;
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
            File f=new File("log/Stock/stock100/100stockTest10.txt");
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
                    System.out.println(v1 + " and " + v2 + " similarity : " + d);
                }

            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // fixme
        try {
            Map<String,List<Double>> S_map = word2VecService.get_inner_Source_EMBDI();
            File sourceFile=new File("log/Stock/stock100/55sourceEMBDI_useModel10.txt");
            sourceFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String,List<Double>>> entrySet = S_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while(it2.hasNext()){
                Map.Entry<String,List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID+" "+stu);
            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    @Test
    public void trainWithPath(){
        SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();

        for(int i = 0;i<sourceNum;i++){
            int temp = i + 1;
            String filePath = "data/stock100/divideSource/source" + temp + ".csv";
            fileList.add(filePath);
        }

        // todo:add truth, default the last one
        String truthPath = "data/stock100/100truth.csv";
        fileList.add(truthPath);

        // time located between both sides of code!
        long preTrainMemory = used();
        long preTrainTime = System.currentTimeMillis();
        // List<Double> vector = word2VecService.train(fileList,3,3,5);
        // fixme : must be the same as path in GenerateNormalizeDistributeSourceTripartite.java, check in folder, or log error
        // fixme: must same as the path in train method !!!
        String walkPath = "data/stock100/walkList"+ version +".txt";
        String graphPath = "data/stock100/graph/55SourceStockGraph.txt";
        word2VecService.trainWithGraphPath(graphPath,walkPath,3,3,60,20000);
        // modelPath
        String modelPath = "model/Tri/stock100/total"+ version +".model";
        // List<Double> vector = word2VecService.trainWithWalks(word2VecService.readListOfListFromFile(walkPath),modelPath);
        List<Double> vector = word2VecService.trainWithLocalWalks(modelPath);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();


        System.out.println(vector.size());



        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 100;
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
            String f_name = "log/Stock/stock100/100stockTest"+version+".txt";
            File f=new File(f_name);
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
                    System.out.println(v1 + " and " + v2 + " similarity : " + d);
                }

            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        // fixme
        try {
            Map<String,List<Double>> S_map = word2VecService.get_inner_Source_EMBDI();
            String sourceFilePath = "log/Stock/stock100/55sourceEMBDI_useModel"+version+".txt";
            File sourceFile=new File(sourceFilePath);
            sourceFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String,List<Double>>> entrySet = S_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while(it2.hasNext()){
                Map.Entry<String,List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID+" "+stu);
            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getSourceEMBDI(SourceTripartiteEmbeddingViaWord2Vec word2VecService) throws Searcher.UnknownWordException {
        String modelPath = "model/Tri/stock100/total"+version+".model";
        // K_map store k-value pair, use saved model get embeddings
        Map<String,List<Double>> K_map = word2VecService.getRandom_Source_Embeddings(modelPath);
        File f=new File("log/Stock/stock100/55sourceEMBDI_useModel"+version+".txt");
        try {
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            Set<Map.Entry<String,List<Double>>> entrySet = K_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while(it2.hasNext()){
                Map.Entry<String,List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID+" "+stu);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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

