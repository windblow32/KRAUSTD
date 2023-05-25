package main.java.embdiTest.DART.embdi_glove;

import com.google.common.collect.ImmutableList;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.GA.Chromosome;
import main.java.Embedding.EMBDI.GA.GeneticAlgorithm;
import main.java.embdiTest.DART.embdi.DART_embdi;
import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.System.arraycopy;
import static java.lang.System.exit;

/**
 * @Description:
 */

public class GA_dart_embdi_glove extends GeneticAlgorithm {

    public static final int NUM = 1 << 26;
    public int existDA;
    public PrintStream out = System.out;
    // fixme : change source
    public String dataPath = "data/monitor0707";
    // fixme : multi type index
    // string 多值
    public List<Integer> stringType_multi_list = new ArrayList<>();
    // num 多值
    public List<Integer> numType_multi_list = new ArrayList<>();
    // public int domain_index = 1;
    public int biaozhushu;
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
    public double Qi;
    public double B_sum;
    public String truthFileName;
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
    public int sourceNum;
    // k是质优度的超参
    public int k = 3;
    public int isDA = 0;
    public Word2VecModel DARTModel;
    // 存储Topk
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = Double.MAX_VALUE;
    public List<String> daTupleList = new ArrayList<>();

    public GA_dart_embdi_glove() {
        super(37);
    }

    public static float Levenshtein(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        //        return 1 - ((float) editDistance / Math.max(a.length(), b.length()));
        return editDis(a, b);
    }
    private void initMultipleList(){
        numType_multi_list.clear();
        stringType_multi_list.clear();
        if(dataPath.equals("data/monitor0707")){
            numType_multi_list.add(100);
            stringType_multi_list.add(2);
            stringType_multi_list.add(3);
        }else if(dataPath.equals("data/camera0707")){
            numType_multi_list.add(100);
            stringType_multi_list.add(3);
        }else {
            numType_multi_list.add(100);
            for(int i = 0;i<D2;i++){
                stringType_multi_list.add(i);
            }
        }
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

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static boolean deleteWithPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            // file not exist
            System.out.println("graphFile is not exist, safe");
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

    @Override
    /*
     * @Description: x的显示表示，将chro转换为用户需要的显示信息，只在print时候和calc时候调用，返回类型是double
     * 7个参数，一个length，6个low和high
     */
    public String changeX(Chromosome chro) {

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
        arraycopy(chro.gene, 0, parameter1, 0, p1_length);
        arraycopy(chro.gene, p1_length, parameter2, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length, parameter3, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 2, parameter4, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 3, parameter5, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 4, parameter6, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 5, parameter7, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 6, parameter8, 0, p8_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length, parameter9, 0, p8_length);

        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2, parameter10, 0, p10_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2 + p10_length, parameter11, 0, p11_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2 + p10_length + p11_length, parameter12, 0, p12_length);
        return p1 + ": " + getPartNum(parameter1)
                + p2 + ": " + getPartNum(parameter2)
                + p3 + ": " + getPartNum(parameter3)
                + p4 + ": " + getPartNum(parameter4)
                + p5 + ": " + getPartNum(parameter5)
                + p6 + ": " + getPartNum(parameter6)
                + p7 + ": " + getPartNum(parameter7)
                + p8 + ": " + getPartNum(parameter8)
                + p9 + ": " + getPartNum(parameter9)
                + p10 + ": " + getPartNum(parameter10)
                + p11 + ": " + getPartNum(parameter11)
                + p12 + ": " + getPartNum(parameter12);

    }

    @Override
    /*
     * @Description: 设计评价函数, 将changeX的参数带入模型中训练，根据训练结果计算估价函数
     */
    public double calculateY(Chromosome chro) {

        version = generation;
        truthFileName = String.valueOf(version);

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
        arraycopy(chro.gene, 0, parameter1, 0, p1_length);
        arraycopy(chro.gene, p1_length, parameter2, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length, parameter3, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 2, parameter4, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 3, parameter5, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 4, parameter6, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 5, parameter7, 0, p2_length);
        arraycopy(chro.gene, p1_length + p2_length * 6, parameter8, 0, p8_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length, parameter9, 0, p8_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2, parameter10, 0, p10_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2 + p10_length, parameter11, 0, p11_length);
        arraycopy(chro.gene, p1_length + p2_length * 6 + p8_length * 2 + p10_length + p11_length, parameter12, 0, p12_length);

//        NormalizeDistributeRunInGA obj = new NormalizeDistributeRunInGA();
        // trans what
        int length = getPartNum(parameter1);
        int AttrDistributeLow = getPartNum(parameter2);
        int AttrDistributeHigh = getPartNum(parameter3);
        int ValueDistributeLow = getPartNum(parameter4);
        int ValueDistributeHigh = getPartNum(parameter5);
        int TupleDistributeLow = getPartNum(parameter6);
        int TupleDistributeHigh = getPartNum(parameter7);
        int dropSourceEdge = getPartNum(parameter8);
        int dropSampleEdge = getPartNum(parameter9);
        int isCBOW = getPartNum(parameter10);
        int dim = getPartNum(parameter11) + 63;
        int windowSize = getPartNum(parameter12) + 1;

        // 数据集列表
        // List<String> fileList = new ArrayList<>();
        // dataset type
        // List<String> DAfileList = new ArrayList<>();
        if (existDA == 1) {
            // DAfileList = initialFileListDA();
            String daSet = dataPath + "/sourceDA/source1.csv";
            daTupleList = initDATupleList(daSet);
        }
        initParameter();
        initMultipleList();
        // attention : 真值发现算法中返回的source weight
        List<Double> weightList = new ArrayList<>();
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        truthFileName = String.valueOf(version);
        String calcTruthPath;
        String t_DApre = null;
        String t_DAafter = null;
        // SOURCENUM
        if (version == 1 && existDA == 1) {
            // add DA
            isDA = 1;
            calcTruthPath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
            DART_embdi_glove dart = new DART_embdi_glove();
            dart.sourceNum = sourceNum;
            dart.dataPath = dataPath;
            DARTModel = dart.distanceForDartUsingMonitorDA(length, 20000, AttrDistributeLow,
                    AttrDistributeHigh,
                    ValueDistributeLow,
                    ValueDistributeHigh,
                    TupleDistributeLow,
                    TupleDistributeHigh,
                    dropSourceEdge,
                    dropSampleEdge,
                    isCBOW,
                    dim,
                    windowSize, truthFileName);
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
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            LocalTime time_after = LocalTime.now();
            t_DAafter = time_after.format(formatter_pre);
        }
        String t_pre = null;
        String t_after = null;
        LocalTime time_pre = LocalTime.now();
        DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_pre = time_pre.format(formatter_pre);
        isDA = 0;
        // calc tempDA
        if (existDA == 1) {
            getTempDA();
        }
        initialFileList();
        calcTruthPath = "E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv";

        DART_embdi_glove dart = new DART_embdi_glove();
        dart.sourceNum = sourceNum;
        dart.dataPath = dataPath;
        DARTModel = dart.distanceForDartUsingMonitorOrigin(length, 20000, AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                isCBOW,
                dim,
                windowSize, truthFileName);

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        LocalTime time_after = LocalTime.now();
        formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_after = time_after.format(formatter_pre);

        // formatResultFromPy(calcTruthPath);
        // fix("E:\\GitHub\\KRAUSTD\\dart\\" + truthFileName + "_truth.csv");

        String[][] calcTruth = readCalcTruth(D1, D2, calcTruthPath);
        this.calcTruth = calcTruth;
        // calcTruth和真值求RMSE
        // golden standard读取

        String[][] goldenStandard = readGoldStandard(D1, D2);
        // 遗传算法的评分
        double score = calcInitFitnessScore();
        // CTD
//        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        // monitor的error rate
        List<Double> error_list = new ArrayList<>();
        // num multi index list and string multi index list

        error_list = errorForMonitor(calcTruth, goldenStandard, D1, D2);
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
        // attention : log path
        String logPath;
        if(dataPath.equals("data/monitor0707")){
            logPath = "log/Tri/DART/monitor/parameter/log" + insertT1 + ".txt";

        }else {
            logPath = "log/Tri/DART/camera/parameter/log" + insertT1 + ".txt";
        }
        File logFile = new File(logPath);
        File weight = new File("E:\\GitHub\\KRAUSTD\\dart\\source_weight.txt");
        try {
            logFile.createNewFile();
            // read weightList from python result
            BufferedReader br = new BufferedReader( new FileReader(weight));
            String str;
            while ((str = br.readLine())!=null){
                weightList.add(Double.parseDouble(str));
            }
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            double error_rate = error_list.get(0);
            if (version == 1) {
                System.out.println("DA过程的断点中断时间 : " + t_DApre + " 至 " + t_DAafter);
            }
            System.out.println("原始数据集（对应第二个断点）起始时间 : " + t_pre + " 至 " + t_after);
            System.out.println("error distance GA : " + RMSEScore);
            System.out.println("error rate GA : " + error_rate);
            System.out.println("rmse: " + error_list.get(2));
            System.out.println("遗传算法代数" + version);
            System.out.println("数据源权重" + weightList);
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
        System.setOut(out);
        double error_rate = rmse;
        judgeFuncList.add(error_rate);
        // 数值升序排序
        Collections.sort(judgeFuncList);

        if (version == 1) {
            return score;
        }
        //
        if (version >= 2) {
            // 开始考虑是否增加罚函数
            int index = judgeFuncList.indexOf(error_rate);
            if (index <= 0) {
                // error 排名在第一,直接采用原始算法
                // use origin ctd, al_kind_flag = 0
                versionList.add(0);
                return score;
            } else if (index >= judgeFuncList.size() - 2) {
                // 排名在最后一名
                // 以后不用罚函数了,直接套用representing learning, al_kind_flag = 1
                versionList.add(1);
                return score;
            } else {

                versionList.add(1);
                // 罚函数的消融实验
                // return CtdService.initFitnessScore;

                // 表示学习嵌入ctd，并且加上罚函数, al_kind_flag = 1
                rmseList = judgeFuncList;
                // 罚函数计算
                if (rmseList.size() < k + 1) {
                    rmseList.add(RMSEScore);
                    if (RMSEScore < minRMSE) {
                        minRMSE = RMSEScore;
                    }
                } else {
                    // 保留TopK最小的
                    rmseList.add(RMSEScore);
                    rmseList.remove(Collections.max(rmseList));
                    // 升序,rmse小的，返回的index小，评估的分数就小，适应度下降，不合理
//            Collections.sort(rmseList);
                    // 降序，rmse小的，index大，评估分数大，适应度高
                    Collections.reverse(rmseList);
                    Word2VecModel m = DARTModel;
                    try {
                        int rank = rmseList.indexOf(RMSEScore);
                        double Qi = (double) (rank + 1) / (k + 1);
                        this.Qi = Qi;
                        double B_sum = 0;
                        for (int s1 = 1; s1 < sourceNum; s1++) {
                            for (int s2 = s1 + 1; s2 <= sourceNum; s2++) {
                                // s1与s2的weight
                                String sourceP = "source_" + s1;
                                String sourceQ = "source_" + s2;
                                double detaSimilarity = Math.abs(distanceUseSavedModel(m, sourceP, sourceQ));
                                double detaWeight = 0;
                                try {
                                    detaWeight = Math.abs(distanceUseSavedModel(DARTModel, sourceP, sourceQ));
                                } catch (IndexOutOfBoundsException e) {
                                    detaWeight = 0;
                                }
                                B_sum += Math.abs(detaSimilarity - detaWeight);
                            }
                        }
                        this.B_sum = B_sum;
                        if (String.valueOf(Qi * B_sum).equals("NaN") || B_sum == 0.0) {
                            return 8.0 + calcInitFitnessScore();
                        }

                        fitScore = Qi * B_sum;
                        return fitScore + calcInitFitnessScore();

                    } catch (NullPointerException e) {
                        // 被抛弃了，排序很低
                        System.exit(-20);
                        return 8.0 + calcInitFitnessScore();
                    }
                }
                return 4 * rmseList.size() + +calcInitFitnessScore();
            }
        }
        return 0.0;
    }

    public void initParameter() {
        File source = new File(dataPath + "/source/source1.csv");
        try {
            FileReader fileReader = new FileReader(source);
            BufferedReader br = new BufferedReader(fileReader);
//            String str;
//            String[] data;
            String attrName = br.readLine();
            D2 = attrName.split(",", -1).length;
            int line = 0;
            while ((br.readLine()) != null) {
                line++;
            }
            D1 = line;
            fileReader.close();
            br.close();
            // DA
            File daFile = new File(dataPath + "/sourceDA/source1.csv");
            if (daFile.exists()) {
                existDA = 1;
                FileReader fr = new FileReader(daFile);
                BufferedReader bufferedReader = new BufferedReader(fr);
                bufferedReader.readLine();
                int DAline = 0;
                while ((bufferedReader.readLine()) != null) {
                    DAline++;
                }
                biaozhushu = DAline;
                bufferedReader.close();
                fr.close();
            } else {
                existDA = 0;
            }
            int flag = 1;
            int num = 1;
            while (flag == 1) {
                File s = new File(dataPath + "/source/source" + num + ".csv");
                if (s.exists()) {
                    num++;
                } else {
                    flag = 0;
                }
            }
            sourceNum = num - 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private void saveDA(int D1, int D2, String path) {
        File saveFile = new File(dataPath + "/da1_order.csv");
        String[][] calcTruth = new String[D1][D2];
        try {
            saveFile.createNewFile();
            PrintStream ps = new PrintStream(saveFile);
            System.setOut(ps);
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            br.mark(100000);
            int row = 0;
            // 第一行已经不是attr
            // br.readLine();
            while ((str = br.readLine()) != null) {
                // data长度不足
                // 存储数据
                data = str.split(",", -1);
                if (daTupleList.contains(data[0])) {
                    continue;
                }
                System.arraycopy(data, 0, calcTruth[row], 0, D2);
                System.out.println(str);
                row++;
            }
            // 重置指针，指向开头，再次遍历，遇到是增强的id就落下
            br.reset();
            while ((str = br.readLine()) != null) {
                data = str.split(",", -1);
                if (daTupleList.contains(data[0])) {
                    // 存储数据
                    data = str.split(",", -1);
                    System.arraycopy(data, 0, calcTruth[row], 0, D2);
                    System.out.println(str);
                    row++;
                }
            }
            ps.close();
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }
    @Deprecated
    private List<String> initialFileListDA() {
        List<String> fileListDA = new ArrayList<>();
        for (int i = 1; i <= sourceNum; i++) {
            String filePath = dataPath + "/sourceDA/source" + i + ".csv";
            fileListDA.add(filePath);
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
    @Deprecated
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
            cosC = 0;

        }
        List<String> distanceList = new ArrayList<>();
        distanceList.add(source + "&" + domain1 + "&" + domain2 + ":" + a * cosC);
        distanceList.add(source + "&" + domain2 + "&" + domain1 + ":" + b * cosC);
        return distanceList;

    }

    /**
     * 读取version文件夹下的所有source数据即可
     *
     * @return
     */
    public List<String> initialFileList() {
        List<String> fileList = new ArrayList<>();
        for (int i = 1; i <= sourceNum; i++) {
            String filePath = dataPath + "/source/source" + i + ".csv";
            fileList.add(filePath);
        }
        fileList.add(dataPath + "/tempDA.csv");

        return fileList;
    }

    public String[][] readCalcTruth(int D1, int D2, String path) {
        String[][] calcTruth = new String[D1][D2];
        String[] data = null;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            // fixme : br limit
            br.mark(100000);
            int row = 0;

            while ((str = br.readLine()) != null) {
                data = str.split(",", -1);
                System.arraycopy(data, 0, calcTruth[row], 0, D2);
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e2) {
            System.out.println("ArrayIndexOutOfBoundsException");
            System.out.println("wrong file path : " + path);
            System.out.println("data : " + data);
            System.out.println("attrNum" + D2);
            System.exit(-1);
        }
        return calcTruth;
    }

    public String[][] readGoldStandard(int D1, int D2) {
        // 这里路径选用金标按照source抽取的allTruth
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = dataPath + "/threetruth.CSV";
        try {
            FileReader fr = new FileReader(goldenStandardPath);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            int row = 0;
            // 扔掉第一行attr
            br.readLine();
            while ((str = br.readLine()) != null) {
                // data长度不足
                if (row < D1) {
                    data = str.split(",", -1);
                    System.out.println(Arrays.toString(data));
                    System.arraycopy(data, 0, goldenStandard[row], 0, D2);

                }
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
    public List<Double> errorForMonitor(String[][] calcTruth, String[][] goldenStandard, int D1, int D2) {
        List<Double> error_list = new ArrayList<>();
        double r = 0;
        int n = 0;
        int error_sample = 0;
        double error_rate = 0;
        double res = 0;
        // 数字类型计算差值，string类型看是否一样,计算累计误差
        double sum = 0;
        // fixme : change source
        int i1 = 0;
        int i2 = 0;
        try {
            for (int i = 0; i < D1; i++) {
                for (int j = 2; j < D2; j++) {
                    i1 = i;
                    i2 = j;
                    if (calcTruth[i][j] == null
                            || goldenStandard[i][j] == null
                            || calcTruth[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("NaN")
                            || calcTruth[i][j].equals("")
                            || goldenStandard[i][j].equals("")
                    ) {
                        sum += 1;
                        res++;
                        error_sample++;
                    } else if (numType_multi_list.contains(j)) {
                        // num,并且有多值
                        String type = calcTruth[i][j];
                        String truthType = goldenStandard[i][j];
                        String[] typeArray;
                        String[] calcArray;
                        calcArray = type.split(";", -1);
                        typeArray = truthType.split(";");
                        double precise = 0;
                        int flag = 0;
                        for (String str : calcArray) {
                            flag = 0;
                            for (String t_str : typeArray) {
                                // error < 5%
                                if ((Math.abs(Double.parseDouble(t_str) - Double.parseDouble(str)) < Double.parseDouble(t_str) * 0.05) && flag != 1) {
                                    precise += 1.0 / calcArray.length;
                                    // 命中了
                                    flag = 1;
                                }
                            }
                        }
                        sum += 1 - precise;
                        res += (1 - precise) * (1 - precise);
                        error_sample += 1 - precise;
                    } else if (stringType_multi_list.contains(j)) {
                        // String类型,并且有多值
                        String type = calcTruth[i][j];
                        String truthType = goldenStandard[i][j];
                        String[] typeArray;
                        String[] calcArray;
                        calcArray = type.split(";", -1);
                        typeArray = truthType.split(";");
                        double precise = 0;
                        int flag = 0;
                        // 只要包含了真值的多值即可
                        for (String str : calcArray) {
                            flag = 0;
                            for (String t_str : typeArray) {
                                if (t_str.equals(str) && flag != 1) {
                                    precise += 1.0 / typeArray.length;
                                    // 命中了
                                    flag = 1;
                                }
                            }
                        }
                        sum += 1 - precise;
                        res += (1 - precise) * (1 - precise);
                        error_sample += 1 - precise;
                    } else {
                        // 连续性数据
                        String str1 = calcTruth[i][j];
                        String str2 = goldenStandard[i][j];
                        double v1 = Double.parseDouble(str1);
                        double v2 = Double.parseDouble(str2);
                        double cha = Math.abs(v1 - v2);
                        if (cha > 0.5) {
                            // same
                            error_sample++;
                        }
                        r += cha * cha;
                        n++;
                        sum += cha;
                        res += cha * cha;
                    }
                }
            }
        } catch (NullPointerException e3) {
            int h = i1 + i2;
        }

        error_rate = 1.0 * error_sample / (D1 * D2);
        // sum = Math.sqrt(sum*sum/(D1*D2));
        // 第一维
        error_list.add(error_rate);
        // ed
        error_list.add(sum);
        // rmse
        error_list.add(Math.sqrt(res / (D1 * D2)));

        return error_list;
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
            return d / (model1 * model2);
        } catch (Searcher.UnknownWordException | NullPointerException e) {
//            e.printStac
//            kTrace();
            System.out.println("word not find");
            return 0;
        }

    }

    public double calcInitFitnessScore() {
        int times = version;
        Searcher search = DARTModel.forSearch();
        String resultFilePath;
        if (isDA == 0) {
//            resultFilePath = "data/ctd/monitor/result/result_" + version + ".csv";
            resultFilePath = "E:\\GitHub\\KRAUSTD\\dart\\" + times + "_truth.csv";

        } else {
//            resultFilePath = "data/ctd/monitor/result/DAresult/result_" + "DA" + ".csv";
            resultFilePath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
        }
        String truthFilePath;
        truthFilePath = dataPath + "/threetruth.csv";
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
            // 读走属性行
            brResult.readLine();
            brTruth.readLine();
            // finished : D2 attr
            int attrKind = D2;
            // finished : 限制只读取前biaozhushu行
            int usedLine = 0;
            while ((str = brResult.readLine()) != null && (strT = brTruth.readLine()) != null && usedLine < biaozhushu) {
                data = str.split(",", -1);
                dataT = strT.split(",", -1);
                if (daTupleList.contains(dataT[0])) {
                    continue;
                }
                // 按照da file对比
                // 维护一个文件存储每次增强的数据的sample id，然后遍历
                for (int a = 0; a < attrKind; a++) {
                    try {

                        // 每次读取新单元格，重新初始化
                        List<List<Float>> listOfSingleWord = new ArrayList<>();
                        // calc list
                        if (data[a].contains(" ")) {
                            String[] singleWordArray = data[a].split(" ");
                            // add each single word split by " "
                            for (int s = 0; s < singleWordArray.length; s++) {
                                listOfSingleWord.add(double2float(search.getRawVector(singleWordArray[s])));
                            }
                        } else {
                            listOfSingleWord.add(double2float(search.getRawVector(data[a])));
                        }
                        // todo : change global embedding
                        // calc pooling embedding并且拼接
                        List<Float> globalEmbedding = new ArrayList<>();
                        globalEmbedding.addAll(meanPooling(listOfSingleWord));

                        // truth pooling
                        List<Float> truthEmbedding = new ArrayList<>();
                        truthEmbedding.addAll(double2float(search.getRawVector(dataT[a])));

                        // end of embdi

                        // end pooling and global embedding
                        // 欧氏距离
                        double totalSingleWord = 0;
                        int globalSize = globalEmbedding.size();
                        for (int i = 0; i < globalSize-1; i++) {
                            try{
                                totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)), 2);
                            }catch (IndexOutOfBoundsException ignored){}
                        }
                        totalSingleWord = Math.sqrt(totalSingleWord);
                        totalGTDistance += totalSingleWord;
                    } catch (Searcher.UnknownWordException e) {
                        totalGTDistance += 0;
                    }catch (ArrayIndexOutOfBoundsException e1){
                        int sss = 1;
                    }
                }
                usedLine++;
            }
            frResult.close();
            frTruth.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isDA == 0&&existDA==1) {
            // 数据增强
            double totalDADistance = 0;
            String DAFilePath = dataPath + "/result/DAresult/result_" + 1 + "_" + times + ".csv";
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

                // 读走属性行
                brResult.readLine();
                brDA.readLine();
                // daFile读走前biaozhushu行,剩下是da
                int passLines = biaozhushu;
                for (int line = 0; line < passLines; line++) {
                    brDA.readLine();
                }
                // 6 attr
                int attrKind = D2;
                // 5
                int usedLine = 0;
                while ((strDA = brDA.readLine()) != null) {

                    dataDA = strDA.split(",", -1);
                    double e1 = 0;
                    double e2 = 0;
                    int numFlag = 0;
                    do {
                        str = brResult.readLine();
                        data = str.split(",", -1);

                        try{
                            e1 = Double.parseDouble(data[0]);
                            e2 = Double.parseDouble(dataDA[0]);
                            if(Math.abs(e1-e2) > 0.5){
                                numFlag = 1;
                            }
                        }catch(NumberFormatException e58){
                            // string类型，不能被解析
                            if(data[0].equals(dataDA[0])){
                                numFlag = 1;
                            }
                        }
                    } while (numFlag == 0);


                    if (!daTupleList.contains(data[0])) {
                        continue;
                    }
                    for (int a = 0; a < attrKind; a++) {
                        try {
                            // 每次读取新单元格，重新初始化
                            List<List<Float>> listOfSingleWord = new ArrayList<>();
                            List<Float> s1List = double2float(search.getRawVector(data[a]));
                            // calc pooling embedding并且拼接
                            List<Float> globalEmbedding = new ArrayList<>();
                            // calc list

                            if (data[a].contains(" ")) {
                                String[] singleWordArray = data[a].split(" ");
                                // add each single word split by " "
                                for (int s = 0; s < singleWordArray.length; s++) {
                                    listOfSingleWord.add(double2float(search.getRawVector(singleWordArray[s])));
                                }
                                globalEmbedding.addAll(meanPooling(listOfSingleWord));
                            } else {
                                globalEmbedding.addAll(s1List);
                            }


                            // truth pooling
                            List<Float> s2List = double2float(search.getRawVector(dataDA[a]));;
                            List<Float> DAEmbedding = new ArrayList<>();
                            DAEmbedding.addAll(s2List);
                            // end pooling and global embedding
                            // 欧氏距离
                            double totalSingleWord = 0;
                            int globalSize = globalEmbedding.size();
                            for (int i = 0; i < globalSize-1; i++) {
                                try{
                                    totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - DAEmbedding.get(i)), 2);
                                }catch (IndexOutOfBoundsException ignored){}
                            }
                            totalSingleWord = Math.sqrt(totalSingleWord);
                            totalDADistance += totalSingleWord;
                        } catch (Searcher.UnknownWordException e) {
                            totalDADistance += 0;
                        }
                    }
                    usedLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalDADistance + totalGTDistance;
        } else {
            return totalGTDistance;
        }

    }

    /**
     * 平均池化输入的embedding
     *
     * @param listOfSingleWord list of embedding to mean pooling
     */
    private List<Float> meanPooling(List<List<Float>> listOfSingleWord) {
        List<Float> result = new ArrayList<>();
        int size = listOfSingleWord.size();
        if(size==1){
            return listOfSingleWord.get(0);
        }
        if(size==2){
            List<Float> list1 = listOfSingleWord.get(0);
            List<Float> list2 = listOfSingleWord.get(1);
            List<Float> res = new ArrayList<>();
            for(int i = 0;i<list1.size();i++){
                res.add(0.5f*(list1.get(i) + list2.get(i)));
            }
            return res;
        }
        int poolWindow = 2;
        int currentIndex = 0;
        try{
            int vectorSize = listOfSingleWord.get(0).size();
            int resultSize = vectorSize / poolWindow;

            List<Float> resultVector = new ArrayList<>();
            for (int i = 0; i < resultSize; i++) {
                float sum = 0.0f;
                for (List<Float> vector : listOfSingleWord) {
                    for (int j = i * poolWindow; j < (i + 1) * poolWindow; j++) {
                        sum += vector.get(j);
                    }
                }
                float average = sum / (listOfSingleWord.size() * poolWindow);
                resultVector.add(average);
            }

            return resultVector;
        }
        catch(IndexOutOfBoundsException e32){
            exit(-32);
        }

        return result;
    }
    @Deprecated
    public boolean detectFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("result file from python detected!");
            return true;
        } else return false;
    }

    public List<String> initDATupleList(String daFilePath) {
        List<String> res = new ArrayList<>();
        File f = new File(daFilePath);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String str;
            String[] data;
            while ((str = br.readLine()) != null) {
                data = str.split(",", -1);
                res.add(data[0]);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
    和数据集中的数据对齐
     */
    public void getTempDA() {
        // 写入的路径
        String tempDAFilePath = dataPath + "/tempDA.csv";
        if (isDA == 0) {
            // 拿到需要修改的数据

            String DAresultPath = "E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv";
            File DAresult = new File(DAresultPath);
            try {
                File tempFile = new File(tempDAFilePath);
                PrintStream ps = new PrintStream(tempFile);
                System.setOut(ps);
                FileReader fr = new FileReader(DAresult);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                while ((str = br.readLine()) != null) {
                    System.out.println(str);
                }
                br.close();
                fr.close();
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    @Test
    public void test() {
        GA_dart_embdi_glove gaImplTest = new GA_dart_embdi_glove();
        gaImplTest.calculate();
        // 最好的代数
        System.out.println(gaImplTest.getGeneI());

    }

}
