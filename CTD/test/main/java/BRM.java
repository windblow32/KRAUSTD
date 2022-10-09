package main.java;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import main.java.Kmean.Cluster;
import main.java.Kmean.KMeansRun;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.medallia.word2vec.Word2VecModel.fromBinFile;
import static main.java.CTD_Algorithm.deleteWithPath;

public class BRM {
    public int sourceNum = 15;
    public Word2VecModel model;
    @Test
    public void produce_sample_embedding(){
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + "_BRM" +".txt";
        String modelPath = "model/Tri/kmeans/totalMin" +"_BRM" + ".model";
        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
        List<String > fileList = initFile();
        word2VecService.train(fileList, graphFilePath, 3, 3, 60);
        model = word2VecService.trainWithLocalWalks(modelPath);
        // get embedding
        ArrayList<float[]> dataSet = word2VecService.getSampleEmbedding(model);

        String sampleEmbeddingFilePath = "log/Tri/sampleEmbedding/weatherLog.txt";
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
        String clusterFilePath = "log/Tri/kmeans/Monitor_clusterLog.txt";
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
//        for (int i = 0; i < sourceNum; i++) {
//            int temp = i + 1;
//            String filePath = "data/stock100/divideSource/source" + temp + ".csv";
//            fileList.add(filePath);
//        }
//        // fixme : 真值添加
//        String truthFilePath = "data/stock100/100truth.csv";
//        fileList.add(truthFilePath);
        sourceNum = 15;
        for (int i = 1; i <= sourceNum; i++) {
            String filePath = "data/ctd/weather/source/source" + i + ".csv";
            fileList.add(filePath);
        }
        String truthFilePath = "data/ctd/weather/weather_truth_sam.csv";
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
    public double loadModel(String modelPath,String v1,String v2) throws IllegalArgumentException{
//        String modelFilePath = "model/Tri/stock100/BRM/totalMin_BRM.model";
//        String modelFilePath = "model/Tri/CTD/monitor/totalMin1_140622.model";
        File f = new File(modelPath);
        try {
            Word2VecModel model1 = fromBinFile(f);
            return distanceUseSavedModel(model1, v1,v2);
//            System.out.println(d);
        } catch (IOException e) {
            System.out.println("model didn't load successfully");
            System.exit(-1);
        }
        return 0;

    }
    @Test
    public void runEMDis(){
        for(int i = 1;i<=2;i++){
            String resultFilePath = "data/distance/calc/weather0"+i+".csv";
            String modelFilePath = "model/Tri/stock100/BRM/totalMin_BRM.model";
            String distanceFilePath = "data/distance/result-weather0"+i+".csv";
            calcDisUsingModel(resultFilePath,modelFilePath,distanceFilePath);
        }
    }

    public void calcDisUsingModel(String resultFilePath,String modelFilePath,String distanceFilePath){
        String word1 = "";
        String word2 = "";

        File disFile = new File(distanceFilePath);
        File resFile = new File(resultFilePath);
        try {
            FileReader fr = new FileReader(disFile);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;

            PrintStream ps = new PrintStream(resFile);
            System.setOut(ps);
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                word1 = data[0];
                word2 = data[1];
                System.out.println(loadModel(modelFilePath,word1,word2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2) throws IllegalArgumentException{
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
