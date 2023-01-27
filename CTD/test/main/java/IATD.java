package main.java;

import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import py4j.GatewayServer;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IATD {
    public Word2VecModel IATDModel;
    // distance constrain between source
    public double p1 = 0.20;
    // distance constrain between source and goldenStandard
    public double p2 = 0.196;
    // source中的实体数
    public int tupleNum;
    public int exitBound = 0;
    public int sourceNum;
    public String dataPath;

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
            fileList.add(dataPath + "/source/source" + i + ".csv");
        }
//        fileList.add("data/iatd/stockForIATDTruth.CSV");
        String truthPath = dataPath + "/allTruth.CSV";
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
            for (int s = 0; s < sourceNum; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 100 entity
                for(int tuple = 0;tuple<tupleNum;tuple++){
                    String tupleName = "row_" + tuple + "_s" + sourceNum;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
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
                                       String truthFileName,
                                       String dataPath){
        this.dataPath = dataPath;
        exitBound = 0;
        // add dataset
        List<String> fileList = new ArrayList<>();

        for (int i = 1; i <= sourceNum; i++) {
//            fileList.add("data/iatd/monitor/sourceDA/source" + i + ".csv");
            fileList.add(dataPath + "/sourceDA/source" + i + ".csv");

        }
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();

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
            for (int s = 0; s < sourceNum; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                // 5entity
                for(int tuple = 0;tuple<tupleNum;tuple++){

                    String tupleName = "row_" + tuple + "_s" + sourceNum;
                    List<String> usefulSourceList = getUsefulSourceSetUsingTupleName(tupleName);
                    for (int t = 0; t < usefulSourceList.size(); t++) {
                        if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
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
    public List<String> getUsefulSourceSetUsingTupleName(String tupleName){
        List<String> result = new ArrayList<>();
        for (int s = 0; s < sourceNum; s++) {
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
