package main.java.Embedding.EMBDI.GA;


import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.DART;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;
import py4j.Gateway;
import py4j.GatewayServer;
import py4j.PythonClient;

import static java.lang.System.arraycopy;

/**
 *@Description:
 */

public class GATestDART extends GeneticAlgorithm{

    // debug use
    public int D1;
    public int D2;
    public String[][] calcTruth = null;
    // calc all data rmse
    public double rmse = 0;
    public double r2 = 0;
    // print fitScore using CTD
    public double fitScore = 0;
    // print extracted rmse using CTD
    public double extractedCTD_RMSE;

    public int Qi;
    public double B_sum;
    public String truthFileName;




    public static final int NUM = 1 << 26;
    public int version;

    // 分批转化成不同的超参，带入评价函数中
    // CBOW一位，dim二位63-255(七位二进制数，加63)，windowSize（三个二进制位）+1

    public String p1 = "length";
    public int p1_length = 6;
    public String p2 = "AttrDistributeLow";
    public String p3 = "AttrDistributeHigh";
    public String p4 = "ValueDistributeLow";
    public String p5 = "ValueDistributeHigh";
    public String p6 = "TupleDistributeLow";
    public String p7 = "TupleDistributeHigh";
    public String p8 = "DropSourceEdge";
    public String p9 = "DropSampleEdge";
    public String p10 = "isCBOW";
    public String p11 = "dim";
    public String p12 = "windowSize";
    public int p8_length = 1;
    public int p2_length = 3;
    public int p10_length = 1;
    public int p11_length = 7;
    public int p12_length = 3;

    public int sourceNum = 5;
    // k是质优度的超参
    public int k = 3;
    public int isDA = 0;
    public Word2VecModel DARTModel;
    // 存储Topk
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = Double.MAX_VALUE;

    public GATestDART() {
        // fixme (fixed) change length of chro
        super(37);
    }

    @Override
    /**
     * @Description: x的显示表示，将chro转换为用户需要的显示信息，只在print时候和calc时候调用，返回类型是double
     * 7个参数，一个length，6个low和high
     */
    public String changeX(Chromosome chro) {

        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1_length];
        boolean[] parameter2 = new boolean[p2_length];
        boolean[] parameter3 = new boolean[p2_length];
        boolean[] parameter4 = new boolean[p2_length];
        boolean[] parameter5 = new boolean[p2_length];
        boolean[] parameter6 = new boolean[p2_length];
        boolean[] parameter7 = new boolean[p2_length];
        boolean[] parameter8 = new boolean[p8_length];
        boolean[] parameter9 = new boolean[p8_length];
        boolean[] parameter10 = new boolean[p10_length];
        boolean[] parameter11 = new boolean[p11_length];
        boolean[] parameter12 = new boolean[p12_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*2,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*3,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*4,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*5,parameter7,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*6,parameter8,0,p8_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length,parameter9,0,p8_length);

        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2,parameter10,0,p10_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2+p10_length,parameter11,0,p11_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2+p10_length+p11_length,parameter12,0,p12_length);
        return p1 + ": " + String.valueOf(getPartNum(parameter1))
                + p2 + ": " + String.valueOf(getPartNum(parameter2))
                + p3 + ": " + String.valueOf(getPartNum(parameter3))
                + p4 + ": " + String.valueOf(getPartNum(parameter4))
                + p5 + ": " + String.valueOf(getPartNum(parameter5))
                + p6 + ": " + String.valueOf(getPartNum(parameter6))
                + p7 + ": " + String.valueOf(getPartNum(parameter7))
                + p8 + ": " + String.valueOf(getPartNum(parameter8))
                + p9 + ": " + String.valueOf(getPartNum(parameter9))
                + p10 + ": " + String.valueOf(getPartNum(parameter10))
                + p11 + ": " + String.valueOf(getPartNum(parameter11))
                + p12 + ": " + String.valueOf(getPartNum(parameter12));

    }


    @Override
    /**
     * @Description: 设计评价函数,将changeX的参数带入模型中训练，根据训练结果计算估价函数
     * todo
     */
    public double calculateY(Chromosome chro) {

        // fixme : 父类的public变量，子类直接使用，数值会随着父类变化吗
        version = generation;
        truthFileName = String.valueOf(version);

        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1_length];
        boolean[] parameter2 = new boolean[p2_length];
        boolean[] parameter3 = new boolean[p2_length];
        boolean[] parameter4 = new boolean[p2_length];
        boolean[] parameter5 = new boolean[p2_length];
        boolean[] parameter6 = new boolean[p2_length];
        boolean[] parameter7 = new boolean[p2_length];
        boolean[] parameter8 = new boolean[p8_length];
        boolean[] parameter9 = new boolean[p8_length];
        boolean[] parameter10 = new boolean[p10_length];
        boolean[] parameter11 = new boolean[p11_length];
        boolean[] parameter12 = new boolean[p12_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*2,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*3,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*4,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*5,parameter7,0,p2_length);
        arraycopy(chro.gene,p1_length+p2_length*6,parameter8,0,p8_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length,parameter9,0,p8_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2,parameter10,0,p10_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2+p10_length,parameter11,0,p11_length);
        arraycopy(chro.gene,p1_length+p2_length*6+p8_length*2+p10_length+p11_length,parameter12,0,p12_length);

//        NormalizeDistributeRunInGA obj = new NormalizeDistributeRunInGA();
        // trans what
        // fixme : parse x into several parts
        int length=getPartNum(parameter1);
        int useNum=20000;
        int AttrDistributeLow = getPartNum(parameter2);
        int AttrDistributeHigh = getPartNum(parameter3);
        int ValueDistributeLow = getPartNum(parameter4);
        int ValueDistributeHigh = getPartNum(parameter5);
        int TupleDistributeLow = getPartNum(parameter6);
        int TupleDistributeHigh = getPartNum(parameter7);
        int dropSourceEdge = getPartNum(parameter8);
        int dropSampleEdge = getPartNum(parameter9);
        int isCBOW = getPartNum(parameter10);
        int dim = getPartNum(parameter11)+63;
        int windowSize = getPartNum(parameter12)+1;

        // 数据集列表
        List<String> fileList = new ArrayList<>();
        // dataset type
        String dataset = "monitor";


        List<String> DAfileList = new ArrayList<>();
        DAfileList = initialFileListDA(dataset);

        // CTD返回的source weight
        List<Double> weightList = new ArrayList<>();
        // todo : 在此处删除可能存在的之前构造的图结构文件，避免印象模型训练
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        truthFileName = String.valueOf(version);
        String calcTruthPath;
        String t_DApre = null;
        String t_DAafter = null;
        // SOURCENUM
        if(version == 1){
            // add DA
            isDA = 1;
            calcTruthPath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
//            NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
//            word2VecService.train(DAfileList,graphPath,3,3,length,20000,AttrDistributeLow,
//                    AttrDistributeHigh,
//                    ValueDistributeLow,
//                    ValueDistributeHigh,
//                    TupleDistributeLow,
//                    TupleDistributeHigh,
//                    dropSourceEdge,
//                    dropSampleEdge,
//                    isCBOW,
//                    dim,
//                    windowSize);
//
//            String modelPath = "model/Tri/DART/monitor/DART_Connection.model";
//            DARTModel = word2VecService.trainWithLocalWalks(modelPath);
//            List<String> domainList = new ArrayList<>();
//            domainList.add("Philips_Electronics");
//            domainList.add("iiyama_North_America");
//            domainList.add("Hannspree");
//            domainList.add("Asus");
//
//            // set output file path
//            File f = new File("log/Tri/DART/monitor/DART_connection.txt");
//            try {
//                f.createNewFile();
//                FileOutputStream fos = new FileOutputStream(f);
//                PrintStream ps = new PrintStream(fos);
//                System.setOut(ps);
//                System.out.println("DA" + truthFileName);
//                // 每个source内部求, 在图中，source从0开始编号
//                for(int s = 0; s < 5; s++){
//                    for(int d = 0 ;d < domainList.size();d++){
//                        for(int t = d+1;t<domainList.size();t++){
//                            String domain1 = domainList.get(d);
//                            String domain2 = domainList.get(t);
//                            List<String> distanceList = new ArrayList<>();
//                            distanceList = getDistanceForDART(DARTModel,"source_"+s, domain1, domain2);
//                            System.out.println(distanceList.get(0));
//                            System.out.println(distanceList.get(1));
//
//                        }
//                    }
//                }
//                ps.close();
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            DART dart = new DART();
            DARTModel = dart.distanceForDartUsingMonitorDA(length,20000,AttrDistributeLow,
                    AttrDistributeHigh,
                    ValueDistributeLow,
                    ValueDistributeHigh,
                    TupleDistributeLow,
                    TupleDistributeHigh,
                    dropSourceEdge,
                    dropSampleEdge,
                    isCBOW,
                    dim,
                    windowSize,truthFileName);
            LocalTime time_pre = LocalTime.now();
            DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
            t_DApre = time_pre.format(formatter_pre);

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
            LocalTime time_after = LocalTime.now();
            t_DAafter = time_after.format(formatter_pre);
            // gatewayServer.shutdown();
            // fixCalc,修正第二列
            // fix("E:\\GitHub\\KRAUSTD\\dart\\DA" + truthFileName + "_truth.csv");
        }
        if(version == 2){
            int a = 0;
        }
        String t_pre = null;
        String t_after = null;

        isDA = 0;
        // calc tempDA
        getTempDA();
        fileList = initialFileList(dataset);
        calcTruthPath = "E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv";

        DART dart = new DART();
        DARTModel = dart.distanceForDartUsingMonitorOrigin(length,20000,AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                isCBOW,
                dim,
                windowSize,truthFileName);
        LocalTime time_pre = LocalTime.now();
        DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_pre = time_pre.format(formatter_pre);
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
        LocalTime time_after = LocalTime.now();
        formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_after = time_after.format(formatter_pre);
        // fix("E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv");
        this.D1 = 20;
        this.D2 = 2;
        // todo : get calctruth from file
        String[][] calcTruth = readCalcTruth(20,2,calcTruthPath);
        this.calcTruth = calcTruth;
        // calcTruth和真值求RMSE
        // golden standard读取

        String[][] goldenStandard = readGoldStandard(D1,D2);
        // todo 有输出了，需要在isDA = 0时候进一步处理该文件
        // 遗传算法的评分
        double score = calcInitFitnessScore();
        // CTD
//        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        // monitor的error rate
        List<Double> error_list = new ArrayList<>();
        error_list = errorForMonitor(calcTruth,goldenStandard,D1,D2);
        double RMSEScore = error_list.get(1);        // monitor 中就是error rate
        rmse = RMSEScore;
        // using CTD print last time's extracted data's rmse
//        extractedCTD_RMSE = CtdService.getRmseForGA();
        // delete calc file
        deleteWithPath("E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv");
        deleteWithPath("E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv");
        // set printStream and rmse output filePath="log/Tri/weightCalcByVex/parameter/1.txt"
        LocalTime time1 = LocalTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String t1 = time1.format(formatter1);
        String[] data1 = t1.split(":");
        String insertT1 = data1[0] + data1[1] + data1[2];

        String logPath = "log/Tri/DART/monitor/parameter/log" + insertT1 + ".txt";
        File logFile = new File(logPath);

        try {
            logFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            double error_rate = error_list.get(0);
            if(version==1){
                System.out.println("DA过程的断点中断时间 : " + t_DApre +" 至 " + t_DAafter);
            }
            System.out.println("原始数据集（对应第二个断点）起始时间 : " + t_pre +" 至 " + t_after);
            System.out.println("error distance GA : " + RMSEScore);
            System.out.println("error rate GA : " + error_rate);

            System.out.println("适应度数值: : " + score);
//            r2 = R_square(calcTruth,goldenStandard,D1,D2);
//            System.out.println("R square GA : " + r2);
            System.out.println("游走长度为 : " + length);
            System.out.println("Attr 正态均值 : " + AttrDistributeLow);
            System.out.println("Attr 正态标准差 : " + AttrDistributeHigh);
            System.out.println("Value 正态均值 : " + ValueDistributeLow);
            System.out.println("Value 正态标准差 : " + ValueDistributeHigh);
            System.out.println("Tuple 正态均值 : " + TupleDistributeLow);
            System.out.println("Tuple 正态标准差 : " + TupleDistributeHigh);
            System.out.println("drop Source ? (0 false, 1 true) : " + dropSourceEdge);
            System.out.println("drop Sample ? (0 false, 1 true) : " + dropSampleEdge);
            System.out.println("using model : " + isCBOW);
            System.out.println("embedding dim : " + dim);
            System.out.println("windowSize : " + windowSize);
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // version.txt > 2
        // read errorList
        List<Double> judgeFuncList = new ArrayList<>();
        String judgeFuncFile = "data/dart/monitor/rmseFile.txt";
        File judgeFuncListFile = new File(judgeFuncFile);
        try {
            // read out
            FileInputStream fi = new FileInputStream(judgeFuncListFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fi);
            judgeFuncList = (List<Double>)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        double error_rate = rmse;
        judgeFuncList.add(error_rate);
        // 数值升序排序
        Collections.sort(judgeFuncList);

        if(version == 1){
            return score;
        }
        //
        // fixme : 对version 2每个超参都这么做吗？
        // 用一个文件传递参数，告诉CTD应该下次运行哪个版本
        int nextVersion = 0;
        String version_list_path = "data/dart/monitor/version.txt";
        File version_list_file = new File(version_list_path);
        try {
            // version_list_file.createNewFile();
            FileOutputStream fos = null;
            if(!version_list_file.exists()){
                version_list_file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(version_list_file);//首次写入获取
            }else{
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(version_list_file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件

            if(version >= 2){
                // 开始考虑是否增加罚函数
                int index = judgeFuncList.indexOf(error_rate);
                if(index <= 1){
                    // error 排名在第一,直接采用原始ctd算法
                    // todo : use origin ctd, al_kind_flag = 0
                    nextVersion = 0;
                    return score;
                }else if(index >= judgeFuncList.size() - 2){
                    // 排名在最后一名
                    // todo : 以后不用罚函数了,直接套用representing learning, al_kind_flag = 1
                    nextVersion = 1;
                    return score;
                }else {

                    nextVersion = 1;
                    // 罚函数的消融实验
                    // return CtdService.initFitnessScore;

                    // todo : 表示学习嵌入ctd，并且加上罚函数, al_kind_flag = 1
                    // read rmseList
                    String rmseStoreFile = "data/dart/monitor/rmseFile.txt";
                    File storeRmseList = new File(rmseStoreFile);
                    try {
                        // read out
                        FileInputStream FI = new FileInputStream(storeRmseList);
                        ObjectInputStream objectInputStream = new ObjectInputStream(FI);
                        List<Double> list = new ArrayList<>();
                        rmseList = (List<Double>)objectInputStream.readObject();
                        objectInputStream.close();
                        FI.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    // 罚函数计算
                    if(rmseList.size() < k+1){
                        rmseList.add(RMSEScore);
                        if(RMSEScore<minRMSE){
                            minRMSE = RMSEScore;
                        }
                        try {
                            // store
                            FileOutputStream outputStream = new FileOutputStream(storeRmseList);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(rmseList);
                            outputStream.close();
                            System.out.println("new rmseList is saved");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    else {
                        // 保留TopK最小的
                        rmseList.add(RMSEScore);
                        rmseList.remove(Collections.max(rmseList));
                        // 升序,rmse小的，返回的index小，评估的分数就小，适应度下降，不合理
//            Collections.sort(rmseList);
                        // 降序，rmse小的，index大，评估分数大，适应度高
                        Collections.reverse(rmseList);
                        Word2VecModel m = DARTModel;
                        try{
                            int rank = rmseList.indexOf(RMSEScore);
                            int Qi = rank/(k+1);
                            this.Qi = Qi;
                            double B_sum = 0;
                            for(int s1 = 1;s1<sourceNum;s1++){
                                for(int s2 = s1 + 1;s2<=sourceNum;s2++){
                                    // s1与s2的weight
                                    String sourceP = "source_"+s1;
                                    String sourceQ = "source_"+s2;
                                    // fixme : 适应度函数中不存在调用新的distance的部分
                                    double detaSimilarity = Math.abs(distanceUseSavedModel(m,sourceP,sourceQ));
                                    double detaWeight = 0;
                                    try{
                                        // fixme : 数据源权重怎么衡量
                                        detaWeight = Math.abs(distanceUseSavedModel(DARTModel,sourceP,sourceQ));
                                    }catch (IndexOutOfBoundsException e){
                                        detaWeight = 0;
                                    }
                                    B_sum += Math.abs(detaSimilarity-detaWeight);
                                }
                            }
                            this.B_sum = B_sum;
                            if(String.valueOf((double)Qi*B_sum).equals("NaN")||B_sum==0.0){
                                return 8.0 + calcInitFitnessScore();
                            }

                            fitScore = (double)Qi*B_sum;
                            return fitScore + calcInitFitnessScore();

                        }catch (NullPointerException e){
                            // 被抛弃了，排序很低
                            return 8.0 + calcInitFitnessScore();
                        }
                    }
                    return 4*rmseList.size() + + calcInitFitnessScore();
                }
            }
            osw.write(nextVersion);
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return 0.0;
    }

    private List<String> initialFileListDA(String dataset) {
        List<String> fileListDA = new ArrayList<>();
        if(dataset.equals("monitor")){
            for (int i = 1; i <= sourceNum; i++) {
                String filePath = "data/dart/monitor/source/source" + i + ".csv";
                fileListDA.add(filePath);
            }
        }
        // fileListDA.add("data/dart/monitor/da-truth.csv");
        return fileListDA;
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


    /**
     * fixme:逐步读取数据
     * 读取version文件夹下的所有source数据即可
     * @return
     */
    public List<String> initialFileList(String dataset){
        List<String> fileList = new ArrayList<>();
        if(dataset.equals("monitor")){
            for (int i = 1; i <= 5; i++) {
                String filePath = "data/dart/monitor/source/source" + i + ".csv";
                fileList.add(filePath);
            }
        }
        // todo add DAresult
        fileList.add("data/dart/monitor/tempDA.csv");

        return fileList;
    }
    public String[][] readCalcTruth(int D1, int D2,String path){
        String[][] calcTruth = new String[D1][D2];
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            int row = 0;
            // 第一行已经不是attr
            // br.readLine();
            while ((str = br.readLine())!=null){
                // data长度不足
                data = str.split(",",-1);
                System.arraycopy(data,0,calcTruth[row],0,D2);
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return calcTruth;

    }
    public String[][] readGoldStandard(int D1, int D2){
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = "data/ctd/monitor/monitor_truth.csv";
        try {
            FileReader fr = new FileReader(goldenStandardPath);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            int row = 0;
            // 扔掉第一行attr
            br.readLine();
            while ((str = br.readLine())!=null){
                // data长度不足
                data = str.split(",",-1);
                System.arraycopy(data,0,goldenStandard[row],0,D2);
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goldenStandard;

    }

    /*
        计算monitor数据集的error rate
     */
    public List<Double> errorForMonitor(String[][] calcTruth, String[][] goldenStandard,int D1,int D2){
        List<Double> error_list = new ArrayList<>();;
        int error_sample = 0;
        double error_rate = 0;
        // 数字类型计算差值，string类型看是否一样,计算累计误差
        double sum = 0;
        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                if(calcTruth[i][j].equals("NaN")
                        ||goldenStandard[i][j].equals("NaN")
                        ||calcTruth[i][j].equals("")
                        || calcTruth[i][j] == null
                        || goldenStandard[i][j].equals("")
                        ||goldenStandard[i][j] == null){
                    sum += 1;
                    error_sample++;
                    continue;
                }
                if(j==1){
                    // String类型,并且有多值
                    String type = calcTruth[i][j];
                    String truthType = goldenStandard[i][j];
                    String[] typeArray;
                    String[] calcArray;
                    calcArray = type.split(";",-1);
                    typeArray = truthType.split(";");
                    double precise = 0;
                    int flag = 0;
                    for(String str : calcArray){
                        flag = 0;
                        for(String t_str : typeArray){
                            if(t_str.equals(str)&&flag != 1){
                                precise += 1.0/calcArray.length;
                                // 命中了
                                flag = 1;
                            }
                        }
                    }
//                    for(String str : typeArray){
//                        double currentDis = Levenshtein(type,str);
//                        if(currentDis < minDis){
//                            minDis = currentDis;
//                        }
//                    }
                    sum += 1-precise;
                    error_sample+=1-precise;
//                }else if(j == 2){
//                    String type = calcTruth[i][j];
//                    String truthType = goldenStandard[i][j];
//                    sum += Levenshtein(type,truthType);
                }else{
                    // 连续性数据
                    String str1 = calcTruth[i][j];
                    String str2 = goldenStandard[i][j];
                    double v1 = Double.parseDouble(str1);
                    double v2 = Double.parseDouble(str2);
                    double cha = Math.abs(v1-v2);
                    if(cha>0.05){
                        // same
                        error_sample++;
                    }
                    sum += cha;
                }
            }
        }
        error_rate = 1.0*error_sample/(D1*D2);

        // 第一位维
        error_list.add(error_rate);
        error_list.add(sum);

        return error_list;
    }

    public static float Levenshtein(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        int editDistance = editDis(a, b);
//        return 1 - ((float) editDistance / Math.max(a.length(), b.length()));
        return editDistance;
    }

    private static int editDis(String a, String b) {

        int aLen = a.length();
        int bLen = b.length();

        if (aLen == 0) return aLen;
        if (bLen == 0) return bLen;

        int[][] v = new int[aLen + 1][bLen + 1];
        for (int i = 0; i <= aLen; ++i) {
            for (int j = 0; j <= bLen; ++j) {
                if (i == 0) {
                    v[i][j] = j;
                } else if (j == 0) {
                    v[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    v[i][j] = v[i - 1][j - 1];
                } else {
                    v[i][j] = 1 + Math.min(v[i - 1][j - 1], Math.min(v[i][j - 1], v[i - 1][j]));
                }
            }
        }
        return v[aLen][bLen];
    }

    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2){
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
        } catch (Searcher.UnknownWordException|NullPointerException e) {
//            e.printStac
//            kTrace();
            System.out.println("word not find");
            return 0;
        }

    }

    // 计算初始适应度，内部包含global embedding生成
    public double calcInitFitnessScore(){
        Searcher search = DARTModel.forSearch();
        String resultFilePath;
        if(isDA == 0){
//            resultFilePath = "data/ctd/monitor/result/result_" + version + ".csv";
            resultFilePath = "E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv";

        }else{
//            resultFilePath = "data/ctd/monitor/result/DAresult/result_" + "DA" + ".csv";
            resultFilePath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
        }
        String truthFilePath;
        if(isDA == 0){
            // ctd
            // truthFilePath = "data/stock100/100truth.csv";
            // monitor
            truthFilePath = "data/dart/monitor/monitor_truth.csv";

        }else{
            // todo : add DA truth file
            // 专门提取da数据写成truth file
            // ctd
            // truthFilePath = "data/stock100/DATruth/trueForDA.csv";
            truthFilePath = "data/ctd/monitor/monitor_truth_da.CSV";
        }

        List<String> daTupleList = new ArrayList<>();
        daTupleList.add("193");


        // find embedding
        File resultFile = new File(resultFilePath);
        File truthFile = new File(truthFilePath);
        // calcResult and golden standard
        double totalGTDistance = 0;
        try {
            // result
            FileReader frResult = new FileReader(resultFile);
            BufferedReader brResult = new BufferedReader(frResult);
            String str;
            String[] data;
            // truth
            FileReader frTruth = new FileReader(truthFile);
            BufferedReader brTruth = new BufferedReader(frTruth);
            String strT;
            String[] dataT;
            // 读走属性行,result 没有属性行
            // brResult.readLine();
            brTruth.readLine();
            // fixme : 2 attr
            int attrKind = 2;
            // fixme : 限制只读取前4行
            int usedLine = 0;
            while((str = brResult.readLine())!=null&&(strT = brTruth.readLine())!=null&&usedLine<4){
                data = str.split(",",-1);
                dataT = strT.split(",",-1);
                if(daTupleList.contains(dataT[0])){
                    continue;
                }
                // 按照da file对比
                // 维护一个文件存储每次增强的数据的sample id，然后遍历
                for(int a = 0;a<attrKind;a++){
                    try{
                        // 每次读取新单元格，重新初始化
                        List<List<Double>> listOfSingleWord = new ArrayList<>();
                        List<Double> s1List = search.getRawVector(data[a]);
                        // calc list
                        if(data[a].contains(" ")){
                            String[] singleWordArray = data[a].split(" ");
                            // add each single word split by " "
                            for(int s = 0;s<singleWordArray.length;s++){
                                listOfSingleWord.add(search.getRawVector(singleWordArray[s]));
                            }
                        }else {
                            listOfSingleWord.add(s1List);
                        }
                        // fixme : change global embedding
                        // calc pooling embedding并且拼接
                        List<Double> globalEmbedding = new ArrayList<>();
                        globalEmbedding.addAll(s1List);
                        globalEmbedding.addAll(meanPooling(listOfSingleWord));
                        // truth pooling
                        List<Double> s2List = search.getRawVector(dataT[a]);
                        List<List<Double>> listOfTruth = new ArrayList<>();
                        listOfTruth.add(s2List);
                        List<Double> truthEmbedding = new ArrayList<>();
                        truthEmbedding.addAll(s2List);
                        truthEmbedding.addAll(meanPooling(listOfTruth));
                        // end pooling and global embedding
                        // 欧氏距离
                        double totalSingleWord = 0;
                        int globalSize = globalEmbedding.size();
                        for(int i = 0;i<globalSize;i++){
                            totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)),2);
                        }
                        totalSingleWord = Math.sqrt(totalSingleWord);
                        totalGTDistance += totalSingleWord;
                    }catch (Searcher.UnknownWordException e){
                        totalGTDistance += 1;
                    }
                }
                usedLine++;
            }
            frResult.close();
            frTruth.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isDA==0){
            // 数据增强
            double totalDADistance = 0;
            String DAFilePath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
            // find embedding
            File DAFile = new File(DAFilePath);
            try {
                // result
                FileReader frResult = new FileReader(resultFile);
                BufferedReader brResult = new BufferedReader(frResult);
                String str;
                String[] data;
                // truth
                FileReader frDA = new FileReader(DAFile);
                BufferedReader brDA = new BufferedReader(frDA);
                String strDA;
                String[] dataDA;

                // daFile读走前19行,剩下全是da
                int passLines = 19;
                for(int line = 0;line<passLines;line++){
                    brDA.readLine();
                }
                // 2 attr
                int attrKind = 2;

                int usedLine = 0;
                while(usedLine<1&&(strDA = brDA.readLine())!=null){

                    dataDA = strDA.split(",",-1);

                    do{
                        // 遍历result，定位到193
                        str = brResult.readLine();
                        data = str.split(",",-1);

                    }while(Math.abs(Double.parseDouble(data[0])-Double.parseDouble(dataDA[0]))>0.5);


                    if(!daTupleList.contains(data[0])){
                        continue;
                    }
                    for(int a = 0;a<attrKind;a++){
                        try{
                            // 每次读取新单元格，重新初始化
                            List<List<Double>> listOfSingleWord = new ArrayList<>();
                            List<Double> s1List = search.getRawVector(data[a]);
                            // calc list
                            if(data[a].contains(" ")){
                                String[] singleWordArray = data[a].split(" ");
                                // add each single word split by " "
                                for(int s = 0;s<singleWordArray.length;s++){
                                    listOfSingleWord.add(search.getRawVector(singleWordArray[s]));
                                }
                            }else {
                                listOfSingleWord.add(s1List);
                            }
                            // calc pooling embedding并且拼接
                            List<Double> globalEmbedding = new ArrayList<>();
                            globalEmbedding.addAll(s1List);
                            globalEmbedding.addAll(meanPooling(listOfSingleWord));
                            // truth pooling
                            List<Double> s2List = search.getRawVector(dataDA[a]);
                            List<List<Double>> listOfDA = new ArrayList<>();
                            listOfDA.add(s2List);
                            List<Double> DAEmbedding = new ArrayList<>();
                            DAEmbedding.addAll(s2List);
                            DAEmbedding.addAll(meanPooling(listOfDA));
                            // end pooling and global embedding
                            // 欧氏距离
                            double totalSingleWord = 0;
                            int globalSize = globalEmbedding.size();
                            for(int i = 0;i<globalSize;i++){
                                totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - DAEmbedding.get(i)),2);
                            }
                            totalSingleWord = Math.sqrt(totalSingleWord);
                            totalDADistance += totalSingleWord;
                        }catch (Searcher.UnknownWordException e){
                            totalDADistance += 1;
                        }
                    }
                    usedLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalDADistance + totalGTDistance;
        }
        else{
            return 0;
        }

    }
    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
    public void fix(String path){
        String str;
        String[] data;
        File file = new File(path);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            while((str = br.readLine())!=null){
                if(str.charAt(0)=='\"'){
                    removeCharAt(str,0);
                    removeCharAt(str,str.length()-1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * 平均池化输入的embedding
     * @param listOfSingleWord list of embedding to mean pooling
     */
    private List<Double> meanPooling(List<List<Double>> listOfSingleWord) {
        List<Double> result = new ArrayList<>();
        int size = listOfSingleWord.size();
        int poolWindow = 2;
        int currentIndex = 0;
        while(currentIndex+poolWindow<size){
            double pool = 0;
            for(int l = 0;l<size;l++){
                // 对于每个embedding
                List<Double> currentList = listOfSingleWord.get(l);
                for(int i = 0;i<poolWindow;i++){
                    // 每次取五个
                    pool += currentList.get(currentIndex + i);
                }
            }
            result.add(pool/(poolWindow*size));
            currentIndex++;
        }
        return result;
    }

    public boolean detectFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            System.out.println("result file from python detected!");
            return true;
        }else return false;
    }
    public static boolean deleteWithPath(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            // file not exist
            System.out.println("graphFile is not exist, safe");
            return false;
        }else {
            if(file.exists() && file.isFile()){
                // file exist
                if(file.delete()){
                    System.out.println("delete graph succeed");
                    return true;
                }
                else {
                    System.out.println("graph delete failed");
                    return false;
                }
            }else {
                System.out.println("input graphPath error!");
                return false;
            }
        }
    }
    /*
    和数据集中的数据对齐
     */
    public void getTempDA(){
        // 写入的路径
        String tempDAFilePath = "data/dart/monitor/tempDA.csv";
        if(isDA==0){
            // 拿到需要修改的数据
            String DAresultPath = "E:\\GitHub\\KRAUSTD\\dart\\DA"+1+"_truth.csv";
            File DAresult = new File(DAresultPath);
            try {
                FileReader fr = new FileReader(DAresult);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                int line = 0;
                // fixme : daResult ，前19行没用
                while(line<19){
                    br.readLine();
                    line++;
                }
                // fixme : 增强的id
                List<String> daTupleList = new ArrayList<>();
                daTupleList.add("193");
                // fixme : 用置信度最强的修复？
                File truthFile = new File("data/dart/monitor/source/source4.csv");
                FileReader truthFr = new FileReader(truthFile);
                BufferedReader truthBr = new BufferedReader(truthFr);
                // read attr
                str = truthBr.readLine();

                File DAFile = new File(tempDAFilePath);
                deleteWithPath(tempDAFilePath);
                DAFile.createNewFile();
                PrintStream ps = new PrintStream(DAFile);
                System.setOut(ps);
                // write attr
                System.out.println(str);

                // 从正常的truth读取str输入到tempDA中，如果tuple在daTupleList中，就用这个替换

                while((str = truthBr.readLine())!=null){
                    data = str.split(",",-1);
                    if(daTupleList.contains(data[0])){
                        // 需要被替换,因为元组是有序的，直接读取DAresult下一行就行
                        //把第二列替换就可以
                        String DAstr = br.readLine();
                        String[] DAdata = DAstr.split(",",-1);
                        data[4] = DAdata[1];
                        String newStr = "";
                        for(int i = 0;i< data.length;i++){
                            newStr += data[i];
                            if(i<data.length-1){
                                newStr += ",";
                            }
                        }
                        System.out.println(newStr);
                    }else{
                        System.out.println(str);
                    }
                }
                ps.close();
                truthBr.close();
                truthFr.close();
                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Test
    public void test(){
        GATestDART gaImplTest = new GATestDART();
        gaImplTest.calculate();
        // 最好的代数
        gaImplTest.getGeneI();

    }

}