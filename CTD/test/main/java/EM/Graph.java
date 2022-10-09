package main.java.EM;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.medallia.word2vec.Word2VecModel.fromBinFile;
import static main.java.CTD_Algorithm.deleteWithPath;

public class Graph {
    public Word2VecModel model;
    @Test
    public void runstock() throws IOException {
        for(int i = 1;i<=9;i++){
            produceStock(i);
        }
    }
    @Test
    public void runweather() throws IOException {
        for(int i = 1;i<=2;i++){
            produceWeather(i);
        }
    }
    public void produceWeather(int  i) throws IOException {
        String filePATH = "data/result-stock01/result-weather0"+i+".csv";
        File f = new File(filePATH);
        String modelPath = "model/Tri/CTD/weather/totalMin1_021135.model";
        File modelFile = new File(modelPath);
        model = fromBinFile(modelFile);
        String outPath = "data/result-stock01/result/weather0"+i+".csv";
        File outputFile = new File(outPath);
        PrintStream ps = new PrintStream(outputFile);
        System.setOut(ps);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                System.out.println(distanceUseSavedModel(model,data[0],data[1]));
            }
            ps.close();
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void produceStock(int  i) throws IOException {
        String filePATH = "data/result-stock01/result-stock0"+i+".csv";
        File f = new File(filePATH);
        String modelPath = "model/Tri/EM/BRM/totalMin_BRM.model";
        File modelFile = new File(modelPath);
        model = fromBinFile(modelFile);
        String outPath = "data/result-stock01/result/stock0"+i+".csv";
        File outputFile = new File(outPath);
        PrintStream ps = new PrintStream(outputFile);
        System.setOut(ps);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                System.out.println(distanceUseSavedModel(model,data[0],data[1]));
            }
            ps.close();
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void run(){
        List<String> dataSource = new ArrayList<>();
        dataSource.add("stock");
        dataSource.add("monitor");
        dataSource.add("camera");
        dataSource.add("weather");
        for(String f:dataSource){
            produce_distance(f);
        }
    }
    public List<String> getDistanceForDART(Word2VecModel model, String source, String domain1, String domain2) {
        Searcher search = model.forSearch();
        // 获取三角形三边
        double a = Math.max(0,distanceUseSavedModel(model,source,domain1));
        double b = Math.max(0,distanceUseSavedModel(model,source,domain2));
        double c = Math.max(0,distanceUseSavedModel(model,domain1,domain2));
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
    public void dart_brm(){
        String dataSet = "monitor";
        setDataSet(dataSet);
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + "_BRM" +".txt";
        String modelPath = "model/Tri/BRM_al/totalMin" +"_BRM" + ".model";
        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
        List<String > fileList = setDataSet(dataSet);
        word2VecService.train(fileList, graphFilePath, 3, 3, 60);
        model = word2VecService.trainWithLocalWalks(modelPath);
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
            System.out.println("DA" + 1);
            // 每个source内部求, 在图中，source从0开始编号
            for(int s = 0; s < 5; s++){
                for(int d = 0 ;d < domainList.size();d++){
                    for(int t = d+1;t<domainList.size();t++){
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(model,"source_"+s, domain1, domain2);
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
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("python E:\\GitHub\\KRAUSTD\\dart\\connect.py");// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void produce_distance(String dataSet) {

        setDataSet(dataSet);
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + "_BRM" +".txt";
        String modelPath = "model/Tri/EM/BRM_al/totalMin" +"_BRM" + ".model";
        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
        List<String > fileList = setDataSet(dataSet);
        word2VecService.train(fileList, graphFilePath, 3, 3, 60);
        model = word2VecService.trainWithLocalWalks(modelPath);
        List<String> node = word2VecService.total_nodes;
        File outputFile = new File("log/Tri/EM/"+dataSet+"/"+dataSet+"_distance.txt");
        PrintStream ps = null;
        try {
            ps = new PrintStream(outputFile);
            System.setOut(ps);
            for(int k = 0;k<node.size()-1;k++){
                for(int q = k+1;q<node.size();q++){
                    String w1 = node.get(k);
                    String w2 = node.get(q);
                    double d = distanceUseSavedModel(model,w1,w2);
                    System.out.println(w1+"&"+w2+":"+d);
                }
            }
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2){

        double d = 0;
        try {
            Searcher search = model.forSearch();
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
    public List<String> setDataSet(String dataSet) {
        List<String> fileList = new ArrayList<>();
        int sourceNum = 0;
        if (dataSet.equals("stock")) {
            sourceNum = 55;
            for (int i = 1; i <= sourceNum; i++) {
                String filePath = "data/stock100/divideSource/source" + i + ".csv";
                fileList.add(filePath);
            }
            return fileList;
        } else if (dataSet.equals("monitor")) {
            sourceNum = 5;
            for (int i = 1; i <= sourceNum; i++) {
                String filePath = "data/dart/monitor/source/source" + i + ".csv";
                fileList.add(filePath);
            }
            return fileList;
        } else if (dataSet.equals("camera")) {
            sourceNum = 5;
            for (int i = 0; i < sourceNum; i++) {
                String filePath = "data/iatd/camera/source/source" + i + ".csv";
                fileList.add(filePath);
            }
            return fileList;
        } else if (dataSet.equals("weather")) {
            sourceNum = 15;
            for (int i = 1; i <= sourceNum; i++) {
                String filePath = "data/ctd/weather/source/source" + i + ".csv";
                fileList.add(filePath);
            }
            return fileList;
        }
        return null;
    }
}
