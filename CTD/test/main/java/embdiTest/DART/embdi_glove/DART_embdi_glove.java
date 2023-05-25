package main.java.embdiTest.DART.embdi_glove;

import com.google.common.collect.ImmutableList;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.java.CTD_Algorithm.deleteWithPath;

public class DART_embdi_glove {
    public String dataPath;
    public Word2VecModel DARTModel;
    // fixme: source number of used dataset
    public int sourceNum;
    public Map<String, float[]> fastText = new HashMap<>();

    @Test
    public void distanceForDart() {
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for (int s = 1; s <= sourceNum; s++) {
            sourceList.add(dataPath + "/source/source" + s + ".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        //  set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4, 1, 50, 4);
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
            for (int s = 0; s < sourceNum; s++) {
                for (int d = 0; d < domainList.size(); d++) {
                    for (int t = d + 1; t < domainList.size(); t++) {
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel, "source_" + s, domain1, domain2);
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
    public void distanceForDartUsingCamera() {
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for (int s = 1; s <= sourceNum; s++) {
            sourceList.add(dataPath + "/source/source" + s + ".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        //  set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4, 1, 50, 4);
        String modelPath = "model/Tri/DART/camera/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
        domainList.add("Nikon");
        domainList.add("Sony");
        domainList.add("Canon");
        domainList.add("Fujifilm");
        domainList.add("Panasonic");

        // set output file path
        File f = new File("log/Tri/DART/camera/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            // 每个source内部求, 在图中，source从0开始编号
            for (int s = 0; s < sourceNum; s++) {
                for (int d = 0; d < domainList.size(); d++) {
                    for (int t = d + 1; t < domainList.size(); t++) {
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel, "source_" + s, domain1, domain2);
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
    public Word2VecModel distanceForDartUsingMonitor() {
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for (int s = 1; s <= sourceNum; s++) {
            sourceList.add(dataPath + "/source/source" + s + ".csv");
        }
        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        //  set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, 60, 1000, 4, 4, 4, 4, 4, 4, 4, 4, 1, 120, 3);
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
            for (int s = 0; s < sourceNum; s++) {
                for (int d = 0; d < domainList.size(); d++) {
                    for (int t = d + 1; t < domainList.size(); t++) {
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel, "source_" + s, domain1, domain2);

                        System.out.println(Mytanh(Double.parseDouble(distanceList.get(0))));
                        System.out.println(Mytanh(Double.parseDouble(distanceList.get(1))));


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

    public double Mytanh(double value) {
        double ex = Math.pow(Math.E, value);// e^x
        double ey = Math.pow(Math.E, -value);//e^(-x)
        double sinhx = ex - ey;
        double coshx = ex + ey;
        double result = sinhx / coshx;
        return result;
    }

    public Word2VecModel distanceForDartUsingMonitorDA(int length, int usenum, int AttrDistributeLow,
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
                                                       String truthFileName) {
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        for (int s = 1; s <= sourceNum; s++) {
            // fixme : step 3 change 15_30_9.0
            sourceList.add(dataPath + "/source/source" + s + ".csv");
        }


        // 反正不用存储图，路径随便写了
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
//        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
//
//        word2VecService.train(sourceList, graphPath, 3,3,length);
//        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
//        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();

        word2VecService.train(sourceList, graphPath, 3, 3, length, 20000, AttrDistributeLow,
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

        if (dataPath.equals("data/monitor0707")) {
            domainList.add("Philips_Electronics");
            domainList.add("iiyama_North_America");
            domainList.add("Hannspree");
            domainList.add("Asus");
        } else if (dataPath.equals("data/camera0707")) {
            domainList.add("Nikon");
            domainList.add("Sony");
            domainList.add("Canon");
            domainList.add("Fujifilm");
            domainList.add("Panasonic");
        }
        // todo : weather
//        domainList.add("Chicago");
//        domainList.add("Dallas");
//        domainList.add("El Paso");
//        domainList.add("Fort Worth");
//        domainList.add("Houston");
//        domainList.add("Indianapolis");
//        domainList.add("Jacksonville");
//        domainList.add("Las Vegas");
//        domainList.add("Los Angeles");
//        domainList.add("Milwaukee");
//        domainList.add("Nashville");
//        domainList.add("Philadelphia");
//        domainList.add("Phoenix");
//        domainList.add("Portland");
//        domainList.add("San Jose");
//        domainList.add("San Diego");
//        domainList.add("Seattle");
//        domainList.add("Washington");



        // fixme set output file path
        File f = new File("log/Tri/DART/weather/DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println("DA" + truthFileName);
            // 每个source内部求, 在图中，source从0开始编号

            for (int s = 0; s < sourceNum; s++) {
                for (int d = 0; d < domainList.size(); d++) {
                    for (int t = d + 1; t < domainList.size(); t++) {
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel, "source_" + s, domain1, domain2);
                        System.out.println(distanceList.get(0));
                        System.out.println(distanceList.get(1));

                    }
                }
            }
            System.out.println(dataPath);
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DARTModel;

    }

    public Word2VecModel distanceForDartUsingMonitorOrigin(int length, int usenum, int AttrDistributeLow,
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
                                                           String truthFileName) {
        List<String> sourceList = new ArrayList<>();
        // not include golden standard
        // fixme : change sources
        for (int s = 1; s <= sourceNum; s++) {
            // fixme : step 6 change source
            sourceList.add(dataPath + "/source/source" + s + ".csv");
        }
        sourceList.add(dataPath + "/tempDA.csv");
        // fixme : step 7 change allTruth
        sourceList.add(dataPath + "/threetruth.CSV");

        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
//        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
//
//        word2VecService.train(sourceList, graphPath, 3,3,length);
//        String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
//        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        //  set parameter
        word2VecService.train(sourceList, graphPath, 3, 3, length, 20000, AttrDistributeLow,
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
        String modelPath = "model/Tri/DART/weather/DART_Connection.model";
        DARTModel = word2VecService.trainWithLocalWalks(modelPath);
        List<String> domainList = new ArrayList<>();
//        domainList.add("Chicago");
//        domainList.add("Dallas");
//        // 下划线
//        domainList.add("El Paso");
//        domainList.add("Fort Worth");
//        domainList.add("Houston");
//        domainList.add("Indianapolis");
//        domainList.add("Jacksonville");
//        domainList.add("Las Vegas");
//        domainList.add("Los Angeles");
//        domainList.add("Milwaukee");
//        domainList.add("Nashville");
//        domainList.add("Philadelphia");
//        domainList.add("Phoenix");
//        domainList.add("Portland");
//        domainList.add("San Jose");
//        domainList.add("San Diego");
//        domainList.add("Seattle");
//        domainList.add("Washington");
        // fixme : camera
        if (dataPath.equals("data/monitor0707")) {
            domainList.add("Philips_Electronics");
            domainList.add("iiyama_North_America");
            domainList.add("Hannspree");
            domainList.add("Asus");
        } else if (dataPath.equals("data/camera0707")) {
            domainList.add("Nikon");
            domainList.add("Sony");
            domainList.add("Canon");
            domainList.add("Fujifilm");
            domainList.add("Panasonic");
        }
        // set output file path
        File f;
        f = new File("E:\\GitHub\\KRAUSTD\\CTD\\log\\Tri\\DART\\DART_connection.txt");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println(truthFileName);
            // 每个source内部求, 在图中，source从0开始编号
            for (int s = 0; s < sourceNum; s++) {
                for (int d = 0; d < domainList.size(); d++) {
                    for (int t = d + 1; t < domainList.size(); t++) {
                        String domain1 = domainList.get(d);
                        String domain2 = domainList.get(t);
                        List<String> distanceList = new ArrayList<>();
                        distanceList = getDistanceForDART(DARTModel, "source_" + s, domain1, domain2);
                        System.out.println(distanceList.get(0));
                        System.out.println(distanceList.get(1));

                    }
                }
            }
            System.out.println(dataPath);
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
        double a = Math.max(0, distanceUseSavedModel(DARTModel, source, domain1));
        double b = Math.max(0, distanceUseSavedModel(DARTModel, source, domain2));
        double c = Math.max(0, distanceUseSavedModel(DARTModel, domain1, domain2));
        // 余弦定理
        double cosC = Math.abs((a * a + b * b - c * c) / (2 * a * b));
        // check bugs
        if (cosC >= 1) {
            int e = 23;
            cosC = 0;
        }
        List<String> distanceList = new ArrayList<>();
        if (a * cosC > 0.0 && a * cosC < 1.0) {
            distanceList.add(source + "&" + domain1 + "&" + domain2 + ":" + Mytanh(a * cosC));
        } else {
            distanceList.add(source + "&" + domain1 + "&" + domain2 + ":" + Mytanh(0.5));
        }
        if (b * cosC > 0.0 && b * cosC < 1.0) {
            distanceList.add(source + "&" + domain2 + "&" + domain1 + ":" + Mytanh(b * cosC));

        } else {
            distanceList.add(source + "&" + domain2 + "&" + domain1 + ":" + Mytanh(0.5));
        }
        return distanceList;
    }

    public void initFastText(){
        try {
            String embeddingFile = "model/Tri/CTD/monitor/1.txt";
            Map<String, float[]> wordEmbeddings = new HashMap<>();

            BufferedReader reader = new BufferedReader(new FileReader(embeddingFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String word = parts[0];
                float[] embedding = new float[parts.length - 1];
                int length = parts.length;
                for (int i = 1; i < length; i++) {
                    embedding[i - 1] = Float.parseFloat(parts[i]);
                }
                wordEmbeddings.put(word, embedding);
            }
            reader.close();
            fastText = wordEmbeddings;
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    public List<Float> array2list(float[] array){
        int embdi_length = 300;
        List<Float> res = new ArrayList<>();
        try{
            for(float n: array){
                res.add(n);
            }
            return res;
        }catch (NullPointerException e){
            for(int i = 0;i<embdi_length;i++){
                res.add(+0.0f);
            }
            return res;
        }
    }

    private List<Float> double2float(ImmutableList<Double> temp){
        List<Float> newList = new ArrayList<>();
        for(double n : temp){
            newList.add((float)n);
        }
        return newList;
    }

    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2) {
        Searcher search = model.forSearch();

        double d = 0;
        try {
            d = search.cosineDistance(s1, s2);
            List<Float> s1List = double2float(search.getRawVector(s1));
            s1List.addAll(array2list(fastText.get(s1)));
            List<Float> s2List = double2float(search.getRawVector(s2));
            s2List.addAll(array2list(fastText.get(s2)));
            float total1 = 0;
            for (float s : s1List) {
                total1 += s * s;
            }
            float model1 = (float) Math.sqrt(total1);
            float total2 = 0;
            for (float s : s2List) {
                total2 += s * s;
            }
            float model2 = (float) Math.sqrt(total2);
            return 1 - Math.abs(d / (model1 * model2));
        } catch (Searcher.UnknownWordException e) {
            return 0;
        }
    }


}
