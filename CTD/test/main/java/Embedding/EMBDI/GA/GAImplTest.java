package main.java.Embedding.EMBDI.GA;


import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.CTD_Algorithm;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeRunInGA;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import main.java.Embedding.EMBDI.TripartiteGraphWithSource.RunInGA;
import org.junit.Test;

import static java.lang.System.arraycopy;
import static java.lang.System.err;

/**
 *@Description:
 */

public class GAImplTest extends GeneticAlgorithm{

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

    public int sourceNum = 55;
    // k是质优度的超参
    public int k = 3;
    // 存储Topk
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = Double.MAX_VALUE;

    public GAImplTest() {
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

        CTD_Algorithm CtdService = new CTD_Algorithm();
        // 数据集列表
        List<String> fileList = new ArrayList<>();
        fileList = initialFileList(version);

        List<String> fileListDA = new ArrayList<>();
        fileListDA = initialFileListDA();

        // 否定约束
        List<String> DCs = new ArrayList<>();
        DCs = initialDC();
        // CTD返回的source weight
        List<Double> weightList = new ArrayList<>();
        // todo : 在此处删除可能存在的之前构造的图结构文件，避免印象模型训练
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        if(version == 1){
            // add DA
            CTD_Algorithm DA_CTDService = new CTD_Algorithm();
            DA_CTDService.update(version,fileListDA,sourceNum,DCs,"THREE",length,AttrDistributeLow,
                    AttrDistributeHigh,
                    ValueDistributeLow,
                    ValueDistributeHigh,
                    TupleDistributeLow,
                    TupleDistributeHigh,
                    dropSourceEdge,
                    dropSampleEdge,
                    rmse,
                    r2,
                    fitScore,
                    extractedCTD_RMSE,
                    isCBOW,
                    dim,
                    windowSize,
                    1,
                    0);
        }

        weightList = CtdService.update(version,fileList,sourceNum,DCs,"THREE",length,AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                rmse,
                r2,
                fitScore,
                extractedCTD_RMSE,
                isCBOW,
                dim,
                windowSize,0,1);

        String[][] calcTruth =  CtdService.getCalcTruth();
        this.calcTruth = calcTruth;
        // calcTruth和真值求RMSE
        // golden standard读取
        int D1 = CtdService.getD1();
        this.D1 = D1;
        int D2 = CtdService.getD2();
        this.D2 = D2;
        String[][] goldenStandard = readGoldStandard(D1,D2);
        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        rmse = RMSEScore;
        // using CTD print last time's extracted data's rmse
//        extractedCTD_RMSE = CtdService.getRmseForGA();
        extractedCTD_RMSE = 233;

        // set printStream and rmse output filePath="log/Tri/weightCalcByVex/parameter/1.txt"
        LocalTime time1 = LocalTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String t1 = time1.format(formatter1);
        String[] data1 = t1.split(":");
        String insertT1 = data1[0] + data1[1] + data1[2];

        String logPath = "log/Tri/weightCalcByVex/parameter/log" + insertT1 + ".txt";
        File logFile = new File(logPath);

        try {
            logFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            System.out.println("RMSE GA : " + RMSEScore);
            r2 = R_square(calcTruth,goldenStandard,D1,D2);
            System.out.println("R square GA : " + r2);
            System.out.println("extractedCTD_RMSE : " + extractedCTD_RMSE);
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // version > 2
        // read errorList
        List<Double> judgeFuncList = new ArrayList<>();
        String judgeFuncFile = "data/stock100/rmseFile.txt";
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
            return CtdService.initFitnessScore;
        }
        //
        // fixme : 对version 2每个超参都这么做吗？
        // 用一个文件传递参数，告诉CTD应该下次运行哪个版本
        String version_list_path = "log/Tri/CTD/version.txt";
        File version_list_file = new File(version_list_path);
        try {
            version_list_file.createNewFile();
            FileInputStream inputVersion = new FileInputStream(version_list_file);
            ObjectInputStream objectVersion = new ObjectInputStream(inputVersion);
            List<Integer> versionList = (List<Integer>)objectVersion.readObject();
            if(version >= 2){
                // 开始考虑是否增加罚函数
                int index = judgeFuncList.indexOf(error_rate);
                if(index <= 1){
                    // error 排名在第一,直接采用原始ctd算法
                    // todo : use origin ctd, al_kind_flag = 0
                    versionList.add(0);
                    return CtdService.initFitnessScore;
                }else if(index >= judgeFuncList.size() - 1){
                    // 排名在最后一名
                    // todo : 以后不用罚函数了,直接套用representing learning, al_kind_flag = 1
                    versionList.add(1);
                    return CtdService.initFitnessScore;
                }else {
                    versionList.add(1);
                    // todo : 表示学习嵌入ctd，并且加上罚函数, al_kind_flag = 1
                    // read rmseList
                    String rmseStoreFile = "data/stock100/rmseFile.txt";
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
                        Word2VecModel m = CtdService.getTriModel();
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
                                        detaWeight = Math.abs(weightList.get(s1)-weightList.get(s2));
                                    }catch (IndexOutOfBoundsException e){
                                        detaWeight = 0;
                                    }
                                    B_sum += Math.abs(detaSimilarity-detaWeight);
                                }
                            }
                            this.B_sum = B_sum;
                            if(String.valueOf((double)Qi*B_sum).equals("NaN")||B_sum==0.0){
                                return 8.0 + CtdService.initFitnessScore;
                            }

                            fitScore = (double)Qi*B_sum;
                            return fitScore + CtdService.initFitnessScore;

                        }catch (NullPointerException e){
                            // 被抛弃了，排序很低
                            return 8.0 + CtdService.initFitnessScore;
                        }
                    }
                    return 4*rmseList.size() + + CtdService.initFitnessScore;
                }
            }
            objectVersion.close();
            inputVersion.close();

            FileOutputStream fileOutputStream = new FileOutputStream(version_list_file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(versionList);
            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return 0.0;
    }

    private List<String> initialFileListDA() {
        List<String> fileListDA = new ArrayList<>();
        for (int i = 0; i < sourceNum; i++) {
            int temp = i + 1;
            String filePath = "data/stock100/divideSourceDA/source" + temp + ".csv";
            fileListDA.add(filePath);
        }
        // fixme : 真值添加
        String truthFilePath = "data/stock100/DATruth/trueForDA.csv";
        fileListDA.add(truthFilePath);
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
    // 测试运行使用！！！
//    GeneticAlgorithmTest test = new GeneticAlgorithmTest();
//        test.calculate();
//        test.getGeneI();

    private double valueFunction(){

        return 5;
    }

    /**
     * fixme:逐步读取数据
     * 读取version文件夹下的所有source数据即可
     * @return
     */
    public List<String> initialFileList(int version){
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
    public List<String> initialDC(){
        List<String> DCs = new ArrayList<>();
        DCs.add("52wk_H > 52wk_L");
        return DCs;
    }
    public String[][] readGoldStandard(int D1, int D2){
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = "data/stock100/100truth.csv";
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

    public double R_square(String[][] calcTruth, String[][] goldenStandard,int D1,int D2) {
        double golden_standard_sum = 0;
        double sum1 = 0;
        double sum2 = 0;

        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                try{
                    if(goldenStandard[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("")
                            || goldenStandard[i][j] == null){
                        golden_standard_sum += 0;
                    }else{
                        double v2 = Double.parseDouble(goldenStandard[i][j]);
                        golden_standard_sum += v2;
                    }

                }catch (NumberFormatException | NullPointerException e) {
                    // fixme 异常处理为0是否合理
                    golden_standard_sum += 0;
                }

            }
        }
        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                try{
                    double v2 = 0;
                    if(goldenStandard[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("")
                            || goldenStandard[i][j] == null){
                        v2 = 0;
                    }else{
                        v2 = Double.parseDouble(goldenStandard[i][j]);
                    }
                    sum2 += (double) (v2-golden_standard_sum);
                }catch (NumberFormatException | NullPointerException e) {
                    // fixme 异常处理为0是否合理
                    sum2 += 0;
                }
            }
        }

        for (int i = 0; i < D1; i++) {
            for (int j = 0; j < D2; j++) {
                try {
                    if (calcTruth[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("NaN")
                            || calcTruth[i][j].equals("")
                            || calcTruth[i][j] == null
                            || goldenStandard[i][j].equals("")
                            || goldenStandard[i][j] == null) {
                        sum1 += 0;
                    }else{
                        double v1 = Double.parseDouble(calcTruth[i][j]);
                        double v2 = Double.parseDouble(goldenStandard[i][j]);
                        sum1 += Math.pow(v1-v2,2);
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    // fixme 异常处理为0是否合理
                    sum1 += 0;
                }
            }
        }
        return 1.0 - (double)(sum1/sum2);
    }


    public double RMSE(String[][] calcTruth, String[][] goldenStandard,int D1,int D2){

        double sum = 0;
        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                try{
                    if(calcTruth[i][j].equals("NaN")
                            ||goldenStandard[i][j].equals("NaN")
                            ||calcTruth[i][j].equals("")
                            || calcTruth[i][j] == null
                            || goldenStandard[i][j].equals("")
                            ||goldenStandard[i][j] == null){
                        sum += 0;
                    }else{
                        double v1 = Double.parseDouble(calcTruth[i][j]);
                        double v2 = Double.parseDouble(goldenStandard[i][j]);
                        sum += Math.pow(Math.abs(v1-v2),2);
                    }
                }catch (NumberFormatException|NullPointerException e){
                    // fixme 异常处理为0是否合理
                    sum += 0;
                }
            }
        }
        sum = sum/(D1*D2);
        sum = Math.sqrt(sum);
        return sum;
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
//            e.printStackTrace();
            System.out.println("word not find");
            return 0;
        }

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




    @Test
    public void test(){
        GAImplTest gaImplTest = new GAImplTest();
        gaImplTest.calculate();
        // 最好的代数
        gaImplTest.getGeneI();

    }

}