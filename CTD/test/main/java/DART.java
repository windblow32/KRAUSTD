package main.java;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;
import py4j.GatewayServer;
import py4j.PythonClient;

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
            sourceList.add("data/ctd/monitor/source/source"+s+".csv");
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

    // 单独测试
    @Test
    public Word2VecModel distanceForDartUsingMonitor(){
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
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4,1,120,3);
        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Philips_Electronics");
        domainList.add("iiyama_North_America");
        domainList.add("Hannspree");
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
        return DARTModel;

    }
    public Word2VecModel distanceForDartUsingMonitorDA(int length,int usenum,int AttrDistributeLow,
                                                           int AttrDistributeHigh,
                                                           int ValueDistributeLow,
                                                           int ValueDistributeHigh,
                                                           int TupleDistributeLow,
                                                           int TupleDistributeHigh,
                                                           int dropSourceEdge,
                                                           int dropSampleEdge,
                                                           int isCBOW,
                                                           int dim,
                                                           int windowSize,
                                                           String truthFileName){
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
        word2VecService.train(sourceList, graphPath, 3,3,length,20000,AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                isCBOW,
                dim,
                windowSize);
        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Philips_Electronics");
        domainList.add("iiyama_North_America");
        domainList.add("Hannspree");
        domainList.add("Asus");

        // set output file path
        File f = new File("log/Tri/DART/monitor/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println("DA" + truthFileName);
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
        return DARTModel;

    }
    public Word2VecModel distanceForDartUsingMonitorOrigin(int length,int usenum,int AttrDistributeLow,
                                                       int AttrDistributeHigh,
                                                       int ValueDistributeLow,
                                                       int ValueDistributeHigh,
                                                       int TupleDistributeLow,
                                                       int TupleDistributeHigh,
                                                       int dropSourceEdge,
                                                       int dropSampleEdge,
                                                       int isCBOW,
                                                       int dim,
                                                       int windowSize,
                                                       String truthFileName){
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for(int s = 1; s<= 5;s++){
            sourceList.add("data/dart/monitor/source/source"+s+".csv");
        }
        sourceList.add("data/dart/monitor/tempDA.csv");
        sourceList.add("data/dart/monitor/monitor-truth-calcScoreUse.CSV");

        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter
        word2VecService.train(sourceList, graphPath, 3,3,length,20000,AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                isCBOW,
                dim,
                windowSize);
        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Philips_Electronics");
        domainList.add("iiyama_North_America");
        domainList.add("Hannspree");
        domainList.add("Asus");

        // set output file path
        File f = new File("log/Tri/DART/monitor/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println(truthFileName);
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
        return DARTModel;

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
        if(a*cosC>0.0&&a*cosC<1.0){
            distanceList.add(source + "&" + domain1 + "&" +domain2 +":" + a*cosC);
        }else{
            distanceList.add(source + "&" + domain1 + "&" +domain2 +":" + 0.5);

        }
        if(b*cosC>0.0&&b*cosC<1.0){
            distanceList.add(source + "&" + domain2 + "&" +domain1 +":" + b*cosC);

        }else{
            distanceList.add(source + "&" + domain2 + "&" +domain1 +":" + 0.5);
        }
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

    @Test
    public void testLoop(){
        for(int i = 0;i<3;i++){
            GatewayServer gatewayServer = new GatewayServer(new DART(),25335);
            //需要启动网关，以便它可以接受传入的 Python 请求：
            gatewayServer.start();
            System.out.println("Gateway Server Started");


        }
    }


}
