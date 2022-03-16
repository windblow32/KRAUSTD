package main.java;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static main.java.CTD_Algorithm.deleteWithPath;

public class DART {
    public Word2VecModel DARTModel;
    public int sourceNum = 5;
    @Test
    public void distanceForDart(){
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for(int s = 1; s<= 5;s++){
            sourceList.add("data/dart/source"+s+".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4,1,50,4);
        String modelPath = "model/Tri/DART/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("literature");
        domainList.add("Study");
        domainList.add("Computer_science");
        domainList.add("Children");
        domainList.add("Science");
        // set output file path
        File f = new File("log/Tri/DART/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            // 每个source内部求, 在图中，source从0开始编号
            for(int s = 0; s < 5; s++){
                for(int d = 0 ;d < domainList.size();d++){
                    for(int t = d+1;t<domainList.size();t++){
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel,"source_"+s, domain1, domain2);
                        System.out.println(distanceList.get(0));
                        System.out.println(distanceList.get(1));

                    }
                }
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void distanceForDartUsingCamera(){
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for(int s = 1; s<= 5;s++){
            sourceList.add("data/dart/camera/source/source"+s+".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4,1,50,4);
        String modelPath = "model/Tri/DART/camera/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Canon");
        domainList.add("Nikon");

        // set output file path
        File f = new File("log/Tri/DART/camera/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            // 每个source内部求, 在图中，source从0开始编号
            for(int s = 0; s < 5; s++){
                for(int d = 0 ;d < domainList.size();d++){
                    for(int t = d+1;t<domainList.size();t++){
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel,"source_"+s, domain1, domain2);
                        System.out.println(distanceList.get(0));
                        System.out.println(distanceList.get(1));

                    }
                }
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void distanceForDartUsingMonitor(){
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for(int s = 1; s<= 5;s++){
            sourceList.add("data/dart/monitor/source/source"+s+".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4,1,50,4);
        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Philips_Electronics");
        domainList.add("iiyama_North_America_Inc");
        domainList.add("Hannspree_Inc");
        domainList.add("Asus");

        // set output file path
        File f = new File("log/Tri/DART/monitor/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            // 每个source内部求, 在图中，source从0开始编号
            for(int s = 0; s < 5; s++){
                for(int d = 0 ;d < domainList.size();d++){
                    for(int t = d+1;t<domainList.size();t++){
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel,"source_"+s, domain1, domain2);
                        System.out.println(distanceList.get(0));
                        System.out.println(distanceList.get(1));

                    }
                }
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<String> getDistanceForDART(Word2VecModel model, String source, String domain1, String domain2) {
        Searcher search = model.forSearch();
        // 获取三角形三边
        double a = Math.max(0,distanceUseSavedModel(DARTModel,source,domain1));
        double b = Math.max(0,distanceUseSavedModel(DARTModel,source,domain2));
        double c = Math.max(0,distanceUseSavedModel(DARTModel,domain1,domain2));
        // 余弦定理
        double cosC = Math.abs((a*a + b*b - c*c)/(2*a*b));
        // check bugs
        if(cosC>=1){
            int e = 23;
            cosC=0;

        }
        List<String> distanceList = new ArrayList<>();
        distanceList.add(source + "&" + domain1 + "&" +domain2 +":" + a*cosC);
        distanceList.add(source + "&" + domain2 + "&" +domain1 +":" + b*cosC);
        return distanceList;



    }
    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2) {
        Searcher search = model.forSearch();
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
            return 1-Math.abs(d / (model1 * model2));
        } catch (Searcher.UnknownWordException e) {
//            e.printStackTrace();
            return 0;
        }

    }


}
