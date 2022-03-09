package main.java;

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
    public double p1 = 0.26;
    // distance constrain between source and goldenStandard
    public double p2 = 0.196;

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
        String entityPath = "data/iatd/entity.CSV";
        File entityFile = new File(entityPath);
        // save entity name
        int[] entityName = new int[100];
        try {
            FileReader readEntity = new FileReader(entityFile);
            BufferedReader brEntity = new BufferedReader(readEntity);
            String entityStr;
            int line = 0;
            while((entityStr = brEntity.readLine())!=null){
                entityName[line] = Integer.parseInt(entityStr);
                line++;
            }
            if(line!=100){
                System.out.println("line is not 99!");
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
        String calcPath = "E:\\tv.csv";
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
                if(sampleInOneSource==100){
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
        for (int i = 0; i < 55; i++) {
            fileList.add("data/iatd/source_" + i + ".csv");
        }
//        fileList.add("data/iatd/stockForIATDTruth.CSV");
        fileList.add(truthPath);
        // train model
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        // fixme: set parameter
        word2VecService.train(fileList, graphPath, 3, 3, 100, 160000, 4, 4, 4, 4, 4, 4, 4, 4);
        String modelPath = "model/Tri/IATD/IATD_Connection.model";
        IATDModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> usefulSourceList = new ArrayList<>();
        for (int s = 0; s < 55; s++) {
            String currentSource = "source_" + s;
            String goldStandardSourceName = "source_55";
            if ((1-similarityUseSavedModel(IATDModel, currentSource, goldStandardSourceName)) > p2) {
                usefulSourceList.add(currentSource);
            }
        }

        File f = new File("log/Tri/IATD/sourceList "+p1+"_"+p2+"_"+".txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            //  fixme : 为每个源的每个entity生成对应的source集合
            for (int s = 0; s < 55; s++) {
                System.out.print("source_"+s+" list: ");
                // compare between current and any other source
                for (int t = 0; t < usefulSourceList.size(); t++) {
                    if((1-similarityUseSavedModel(IATDModel,"source_"+s, usefulSourceList.get(t)))>p1){
                        // fixme : careful process using python!!!
                        System.out.print("source_"+t+";");
                    }
                }
                System.out.print("\n");
            }
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        IATD app = new IATD();
        // py4j服务
        GatewayServer gatewayServer = new GatewayServer(app);
        gatewayServer.start();
        System.out.println("Gateway Server Started");
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
