package EMBDI.TripartiteNormalizeDistributeGraph;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;


/**
 * test for embedding
 */
public class NormalizeDistributeSourceTripartiteEmbeddingViaWord2VecTest {
    public static int sourceNum = 55;
    public static int tupleNum = 100;
    // file name
    public static int version = 1;
    public static int AttrDistributeLow = 1;
    public static int AttrDistributeHigh = 5;
    public static int ValueDistributeLow = 1;
    public static int ValueDistributeHigh = 2;
    public static int TupleDistributeLow = 1;
    public static int TupleDistributeHigh = 4;
    public static String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";

    @Test
    public void test1() {
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();

        for (int i = 0; i < sourceNum; i++) {
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
        word2VecService.train(fileList, graphFilePath, 3, 3, 60, 20000, AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh);
        // fixme: must same as the path in train method !!!
        String walkPath = "data/stock100/weightCalcByVex/walkListMin" + version + ".txt";
        String modelPath = "model/Tri/stock100/weightCalcByVex/totalMin" + version + ".model";
        List<Double> vector = word2VecService.trainWithLocalWalks(modelPath);
        long afterTrainTime = System.currentTimeMillis();
        long afterTrainMemory = used();

        System.out.println(vector.size());


        // word2vec training time
        long trainTime = afterTrainTime - preTrainTime;
        // word2vec training memory
        long trainMemory = afterTrainMemory - preTrainMemory;
        int EMSize = 100;
        Map<String, List<Double>> K_map = new HashMap<>();
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
            File f = new File("log/Stock/stock100/weightCalcByVex/100stockMinTest" + version + ".txt");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String, List<Double>>> entrySet = K_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while (it2.hasNext()) {
                Map.Entry<String, List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID + " " + stu);
            }
            System.out.println("model training memory is : " + trainMemory);
            System.out.println("model training time is : " + trainTime);
            System.out.println("random " + realSize + " embeddings calc memory is : " + embeddingCalcMemory);
            System.out.println("random " + realSize + " embeddings calc time is : " + embeddingTime);
            // node in K_map : calc distance between each of 2

            Set<String> nameSet = new HashSet<>(K_map.keySet());
            Iterator<String> nameItor = nameSet.iterator();
            List<String> nameList = new ArrayList<>();
            while (nameItor.hasNext()) {
                String name = nameItor.next();
                nameList.add(name);
            }
            int nameListSize = nameList.size();
            String v1;
            String v2 = null;
            for (int i = 0; i < nameListSize; i++) {
                v1 = nameList.get(i);
                for (int j = i + 1; j < nameListSize; j++) {
                    v2 = nameList.get(j);
                    double d = word2VecService.distance(v1, v2);
                    System.out.println(v1 + " and " + v2 + " similarity : " + d);
                }

            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // source embedding
        try {
            Map<String, List<Double>> S_map = word2VecService.get_inner_Source_EMBDI();
            File sourceFile = new File("log/Stock/stock100/weightCalcByVex/55sourceEMBDI_Min" + version + ".txt");
            sourceFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String, List<Double>>> entrySet = S_map.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while (it2.hasNext()) {
                Map.Entry<String, List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID + " " + stu);
            }
            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return memory cost current
     */
    public static long used() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        return total - free;
    }

}