package main.java.dart.weather.all;

import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import py4j.GatewayServer;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IATD_all {
    public Word2VecModel IATDModel;
    // distance constrain between source
    public double p1 = 0.20;
    // distance constrain between source and goldenStandard
    public double p2 = 0.196;

    public int entityNum = 20;
    // source中的实体数
    public int tupleNum = 150;
    public int exitBound = 0;
    public int sourceNum = 15;

    public static boolean deleteWithPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            // file not exist
            System.out.println("safe, graphFile is not exist");
            return false;
        } else {
            if (file.exists() && file.isFile()) {
                // file exist
                if (file.delete()) {
                    System.out.println("delete graph succeed");
                    return true;
                } else {
                    System.out.println("graph delete failed");
                    return false;
                }
            } else {
                System.out.println("input graphPath error!");
                return false;
            }
        }
    }
    @Test
    public void produce_dataset() {
        Double[] input = new Double[1000];
        // get entity name list
        String entityPath = "data/iatd/entity/monitor-entity.CSV";
        File entityFile = new File(entityPath);
        // save entity name
        int[] entityName = new int[20];
        try {
            FileReader readEntity = new FileReader(entityFile);
            BufferedReader brEntity = new BufferedReader(readEntity);
            String entityStr;
            int line = 0;
            while((entityStr = brEntity.readLine())!=null){
                entityName[line] = Integer.parseInt(entityStr);
                line++;
            }
            if(line!=20){
                System.out.println("line is not 20!");
            }
            else {
                System.out.println("read entity name successfully");
            }
            brEntity.close();
            readEntity.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // process input file from python
        // fixme : python run first and save file in java's path
        String calcPath = "E:\\GitHub\\KRAUSTD\\IATD\\tv.csv";
        File calcTruth = new File(calcPath);
        try {
            FileReader frCalc = new FileReader(calcTruth);
            BufferedReader brCalc = new BufferedReader(frCalc);
            String strCalc;
            int line = 0;
            while((strCalc = brCalc.readLine())!=null){
                input[line] = Double.parseDouble(strCalc);
                line++;
            }
            // fixme
            if(line!=1000){
                System.out.println("read calc truth num error ");
            }else {
                System.out.println("read calc truth successfully");
            }
            brCalc.close();
            frCalc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sampleInOneSource = 0;
        String truthPath = "data/iatd/truth.csv";
        File f1 = new File(truthPath);
        try {
            f1.createNewFile();
            PrintStream ps1 = new PrintStream(f1);
            System.setOut(ps1);
            // output attr name;
            System.out.println("entity,domain");
            for(int i = 0;i<input.length;i++){
                if(input[i]==0.0){
                    continue;
                }
                System.out.println(entityName[sampleInOneSource]+","+input[i]);
                sampleInOneSource++;
                if(sampleInOneSource==20){
                    // 100个实体了，exit
                    break;
                }

            }
            ps1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        // add dataset
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fileList.add("data/iatd/source_" + i + ".csv");
        }
//        fileList.add("data/iatd/stockForIATDTruth.CSV");
        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter, search length, 3 new parameter
        word2VecService.train(fileList, graphPath, 3, 3, 100, 160000, 4, 4, 4, 4, 4, 4, 4, 4,1,50,4);
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);


        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);

            //  fixme : 为每个源的每个entity生成对应的source集合, 100 entity 55source,
            // todo : 内部,分割，entity之间;分割
            for (int s = 0; s < 5; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 100 entity
                for(int tuple = 0;tuple<100;tuple++){

                    String tupleName = "row_" + tuple + "_s" + 5;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                            // fixme : careful process using python!!!
                            System.out.print(t+",");
                        }
                    }
                    System.out.print(";");
                }

                System.out.print("\n");
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Word2VecModel iatdUseOrigin(int length,int usenum,int AttrDistributeLow,
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
        exitBound =0;
        // add dataset
        List<String> fileList = new ArrayList<>();
        // i <= sourceNum
        for (int i = 1; i <= sourceNum; i++) {
//            fileList.add("data/iatd/monitor/source/source" + i + ".csv");
            fileList.add("data/new_weather/15_30_9.0/source/source" + i + ".csv");
        }
//        fileList.add("data/iatd/stockForIATDTruth.CSV");
        // fixme truthPath
        String truthPath = "data/new_weather/15_30_9.0/allTruth.CSV";
        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
//        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
//
//        word2VecService.train(sourceList, graphPath, 3,3,length);
//        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
//        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter, search length, 3 new parameter
        word2VecService.train(fileList, graphPath, 3, 3, length, usenum,AttrDistributeLow,
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
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);


        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println(truthFileName);
            //  fixme : 为每个源的每个entity生成对应的source集合, 100 entity 55source,
            // todo : 内部,分割，entity之间;分割
            // fixme : s < sourceNum
            for (int s = 0; s < 15; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 100 entity
                // fixme : tuple < sizeof(source)
                for(int tuple = 0;tuple<150;tuple++){
                    // fixme : tupleName = "row_" + tuple + "_s" + sourceNum(15)
                    String tupleName = "row_" + tuple + "_s" + 15;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                            // fixme : careful process using python!!!
                            exitBound++;
                            System.out.print(t+",");
                        }
                    }
                    System.out.print(";");
                }

                System.out.print("\n");
            }
            ps.close();
            fos.close();
            File exitFile = new File("log/Tri/IATD/exitBound.txt");
            PrintStream e1 = new PrintStream(exitFile);
            System.setOut(e1);
            System.out.println(exitBound);
            e1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IATDModel;
    }

    public Word2VecModel iatdUseDA(int length,int usenum,int AttrDistributeLow,
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
        exitBound = 0;
        // add dataset
        List<String> fileList = new ArrayList<>();

        for (int i = 1; i <= sourceNum; i++) {
//            fileList.add("data/iatd/monitor/sourceDA/source" + i + ".csv");
            fileList.add("data/new_weather/15_30_9.0/sourceDA/source" + i + ".csv");

        }
        // fixme truthPath
//        String truthPath = "data/iatd/monitor/sourceDA/DAtruth.CSV";
//        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();

        // fixme: set parameter, search length, 3 new parameter
        // 新的三分图
        word2VecService.train(fileList, graphPath, 3, 3, length, usenum,AttrDistributeLow,
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
        // BRM_al
//        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
//        word2VecService.train(fileList, graphPath, 3, 3, length);
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);


        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println("DA"+truthFileName);
            //  fixme : 为每个源的每个entity生成对应的source集合, 100 entity 55source,
            // todo : 内部,分割，entity之间;分割
            for (int s = 0; s < sourceNum; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 5entity
                // fixme : source中的实体数
                for(int tuple = 0;tuple<tupleNum;tuple++){

                    String tupleName = "row_" + tuple + "_s" + sourceNum;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                            // fixme : careful process using python!!!
                            exitBound++;
                            System.out.print(t+",");
                        }
                    }
                    System.out.print(";");
                }

                System.out.print("\n");
            }
            ps.close();
            fos.close();
            File exitFile = new File("log/Tri/IATD/exitBound.txt");
            PrintStream e1 = new PrintStream(exitFile);
            System.setOut(e1);
            System.out.println(exitBound);
            e1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IATDModel;
    }

    public Word2VecModel iatdUseDA_camera(int length,int usenum,int AttrDistributeLow,
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
        exitBound = 0;
        int sourceNum = 6;
        // add dataset
        List<String> fileList = new ArrayList<>();
        for (int i = 1; i <= sourceNum; i++) {
            fileList.add("data/iatd/camera/sourceDA/source" + i + ".csv");
        }
        // fixme truthPath
        String truthPath = "data/iatd/camera/sourceDA/DAtruth.CSV";
        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter, search length, 3 new parameter
        word2VecService.train(fileList, graphPath, 3, 3, length, usenum,AttrDistributeLow,
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
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);


        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println("DA"+truthFileName);
            //  fixme : 为每个源的每个entity生成对应的source集合, 100 entity 55source,
            // todo : 内部,分割，entity之间;分割
            for (int s = 0; s < sourceNum; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 5entity
                for(int tuple = 0;tuple<20;tuple++){

                    String tupleName = "row_" + tuple + "_s" + 5;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                            // fixme : careful process using python!!!
                            exitBound++;
                            System.out.print(t+",");
                        }
                    }
                    System.out.print(";");
                }

                System.out.print("\n");
            }
            ps.close();
            fos.close();
            File exitFile = new File("log/Tri/IATD/exitBound.txt");
            PrintStream e1 = new PrintStream(exitFile);
            System.setOut(e1);
            System.out.println(exitBound);
            e1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IATDModel;
    }
    public Word2VecModel iatdUseOrigin_camera(int length,int usenum,int AttrDistributeLow,
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
        exitBound =0;
        int sourceNum = 6;
        // add dataset
        List<String> fileList = new ArrayList<>();
        for (int i = 1; i <= sourceNum; i++) {
            fileList.add("data/iatd/camera/source/source" + i + ".csv");
        }
//        fileList.add("data/iatd/stockForIATDTruth.CSV");
        // fixme truthPath
        String truthPath = "data/iatd/camera/camera_truth.csv";
        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter, search length, 3 new parameter
        word2VecService.train(fileList, graphPath, 3, 3, length, usenum,AttrDistributeLow,
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
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);


        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println(truthFileName);
            //  fixme : 为每个源的每个entity生成对应的source集合, 100 entity 55source,
            // todo : 内部,分割，entity之间;分割
            for (int s = 0; s < sourceNum; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 100 entity
                for(int tuple = 0;tuple<20;tuple++){

                    String tupleName = "row_" + tuple + "_s" + 5;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                            // fixme : careful process using python!!!
                            exitBound++;
                            System.out.print(t+",");
                        }
                    }
                    System.out.print(";");
                }

                System.out.print("\n");
            }
            ps.close();
            fos.close();
            File exitFile = new File("log/Tri/IATD/exitBound.txt");
            PrintStream e1 = new PrintStream(exitFile);
            System.setOut(e1);
            System.out.println(exitBound);
            e1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IATDModel;
    }


    public static void main(String[] args){
        main.java.IATD app = new main.java.IATD();
        // py4j服务
        GatewayServer gatewayServer = new GatewayServer(app);
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }

    public List<String> getUsefulSourceSetUsingTupleName(String tupleName){
        List<String> result = new ArrayList<>();
        for (int s = 0; s < 15; s++) {
            String currentSource = "source_" + s;
            double sim = similarityUseSavedModel(IATDModel, currentSource, tupleName);
//            if(sim == 0.0){
//                System.out.println("vector is not exist");
//            }
            if ((1-sim) > p2) {
                result.add(currentSource);
            }
        }
        return result;
    }

    public double similarityUseSavedModel(Word2VecModel model, String s1, String s2) {
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
            return Math.abs(d / (model1 * model2));
        } catch (Searcher.UnknownWordException e) {
//            e.printStackTrace();
            return 0;
        }

    }

}

