package main.java;

import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import main.java.Kmean.Cluster;
import main.java.Kmean.KMeansRun;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static main.java.CTD_Algorithm.deleteWithPath;

public class BRM {
    public int sourceNum = 55;
    public Word2VecModel model;
    @Test
    public void produce_sample_embedding(){
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + "_BRM" +".txt";
        String modelPath = "model/Tri/stock100/BRM/totalMin" +"_BRM" + ".model";
        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
        List<String > fileList = initFile();
        word2VecService.train(fileList, graphFilePath, 3, 3, 60);
        model = word2VecService.trainWithLocalWalks(modelPath);
        // get embedding
        ArrayList<float[]> dataSet = word2VecService.getSampleEmbedding(model);

        String sampleEmbeddingFilePath = "log/Tri/sampleEmbedding/sampleEmbeddingLog.txt";
        File sampleEmbedding = new File(sampleEmbeddingFilePath);
        try {
            PrintStream psForSample = new PrintStream(sampleEmbedding);
            System.setOut(psForSample);
            for(float[] array : dataSet){
                for(int s = 0;s<array.length;s++){
                    System.out.print(array[s] + " ; ");
                }
                // 换行
                System.out.println(" ");
            }
            psForSample.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        KMeansRun kRun =new KMeansRun(3, dataSet);

        Set<Cluster> clusterSet = kRun.run();
        // change output stream
        String clusterFilePath = "log/Tri/kmeans/clusterLog.txt";
        File clusterResult = new File(clusterFilePath);
        try {
            PrintStream ps = new PrintStream(clusterResult);
            System.setOut(ps);
            System.out.println("单次迭代运行次数："+kRun.getIterTimes());
            for (Cluster cluster : clusterSet) {
                System.out.println(cluster);
            }
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private List<String> initFile() {
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < sourceNum; i++) {
            int temp = i + 1;
            String filePath = "data/stock100/divideSource/source" + temp + ".csv";
            fileList.add(filePath);
        }
        // fixme : 真值添加
        String truthFilePath = "data/stock100/100truth.csv";
        fileList.add(truthFilePath);
        return fileList;
    }
    @Test
    public void testgetPartNum(){
        boolean[] a = new boolean[4];
        a[0] = true;
        a[1] = true;
        a[2] = false;
        a[3] = true;
        System.out.print(getPartNum(a));
    }
    public int getPartNum(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int num = 0;
        for (boolean bool : array) {
            num <<= 1;
            if (bool) {
                num += 1;
            }
        }
        return num;
    }
}
