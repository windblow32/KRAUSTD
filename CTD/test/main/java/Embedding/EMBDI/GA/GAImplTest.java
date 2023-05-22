package main.java.Embedding.EMBDI.GA;


import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.CTD_Algorithm;
import main.java.Embedding.fastText.LoadModel;
import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.System.arraycopy;
import static java.lang.System.exit;

/**
 * @Description:
 */

public class GAImplTest extends GeneticAlgorithm {

    public static final int NUM = 1 << 26;
    // debug use
    // fixme : change source
    public String dataPath = "data/monitor0707";
    public int existDA = 1;
    // fixme : change source 标注数
    public int biaozhushu = 20;
    public int zengqiangshu = 20;
    // fixme : change source 数据源
    public int sourceNum = 5;
    // k是质优度的超参
    public int k = 7;

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

    // 存储Top k
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = Double.MAX_VALUE;
    private String attrName;
    // string 多值
    public List<Integer> stringType_multi_list = new ArrayList<>();
    // num 多值
    public List<Integer> numType_multi_list = new ArrayList<>();

    public GAImplTest() {
        super(37);
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
    /**
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
    /**
     * @Description: 设计评价函数, 将changeX的参数带入模型中训练，根据训练结果计算估价函数
     */
    public double calculateY(Chromosome chro) {

        version = generation;

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
        int useNum = 20000;
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
        initParameter();
        initMultipleList();
        CTD_Algorithm CtdService = new CTD_Algorithm();
        // 数据集列表
        List<String> fileList;
        // fixme : 更换整体数据集时变化 ，dataset type
        fileList = initialFileList();

        List<String> fileListDA;
        fileListDA = initialFileListDA();

        // 否定约束
        List<String> DCs;
        DCs = initialDC();
        // CTD返回的source weight
        List<Double> weightList;
        // 在此处删除可能存在的之前构造的图结构文件，避免印象模型训练
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String t_DApre = null;
        String t_DAafter = null;
        if(version == 1){
            versionList.add(0);
        }
        // sourceNum
        if (version == 1 && existDA == 1) {
            // time start
            LocalTime time_pre = LocalTime.now();
            DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
            t_DApre = time_pre.format(formatter_pre);
            // add DA
            CTD_Algorithm DA_CTDService = new CTD_Algorithm();
            DA_CTDService.update(biaozhushu, zengqiangshu, dataPath, existDA, version, fileListDA, sourceNum, DCs, "THREE", length, AttrDistributeLow,
                    AttrDistributeHigh,
                    ValueDistributeLow,
                    ValueDistributeHigh,
                    TupleDistributeLow,
                    TupleDistributeHigh,
                    dropSourceEdge,
                    dropSampleEdge,
                    isCBOW,
                    dim,
                    windowSize,
                    versionList,
                    1,
                    1,
                    fastText);
            // time end
            LocalTime time_after = LocalTime.now();
            t_DAafter = time_after.format(formatter_pre);

        }
        String t_pre;
        String t_after;
        LocalTime time_pre = LocalTime.now();
        DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_pre = time_pre.format(formatter_pre);
        // fixme : only ctd把最后一个参数设置为0
        weightList = CtdService.update(biaozhushu, zengqiangshu, dataPath, existDA, version, fileList, sourceNum + 1, DCs, "THREE", length, AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge,
                isCBOW,
                dim,
                windowSize,
                versionList,0, 1,
                fastText);
        // time end
        LocalTime time_after = LocalTime.now();
        formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
        t_after = time_after.format(formatter_pre);

        String[][] calcTruth = CtdService.getCalcTruth();
        this.calcTruth = calcTruth;
        // calcTruth和真值求RMSE
        // golden standard读取
        int D1 = CtdService.getD1();
        this.D1 = D1;
        int D2 = CtdService.getD2();
        this.D2 = D2;
        String[][] goldenStandard = readGoldStandard(D1, D2);
        // CTD
//        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        // monitor
        List<Double> error_list;
        error_list = errorForMonitor(calcTruth, goldenStandard, D1, D2);
        // attention : error list
        double RMSEScore = error_list.get(1);        // monitor 中就是error rate
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
        // fixme : log路径
        String logPath = "log/Tri/CTD/monitor/parameter/log" + insertT1 + ".txt";
        File logFile = new File(logPath);
        try {
            logFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            if (version == 1) {
                System.out.println("DA过程的CTD时间 : " + t_DApre + " 至 " + t_DAafter);
            }
            System.out.println("原始数据集（对应第二个断点）CTD时间 : " + t_pre + " 至 " + t_after);
            System.out.println("遗传算法代数" + version);
            System.out.println("数据源权重" + weightList);
            System.out.println("随机游走前时间" + CtdService.timeBeforeWalk);
            System.out.println("随机游走后时间(开始训练)" + CtdService.timeAfterWalk);
            System.out.println("训练结束时间" + CtdService.timeAfterTrain);
            System.out.println("distance1计算结束时间(与训练结束时间做差查看利用模型计算的时间)" + CtdService.timeAfterDis1);
            System.out.println("distance2计算结束时间(与distance1做差查看利用模型计算的时间)" + CtdService.timeAfterDis2);
            System.out.println("error rmse : " + error_list.get(2));
            System.out.println("error rate : " + error_list.get(0));
            System.out.println("error distance: " + error_list.get(1));
            System.out.println("适应度: " + CtdService.initFitnessScore);
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
        // 恢复输出流
        System.setOut(CtdService.out);
        double error_rate = rmse;
        judgeFuncList.add(error_rate);
        // 数值升序排序
        Collections.sort(judgeFuncList);
        if (version == 1) {
            return CtdService.initFitnessScore;
        }
        //
        // fixme : 更换整体数据集时，变化version路径 ，用一个文件传递参数，告诉CTD应该下次运行哪个版本
        if (version >= 2) {
            // 开始考虑是否增加罚函数
            int index = judgeFuncList.indexOf(error_rate);
            if (index <= 0) {
                // error 排名在第一,直接采用原始ctd算法
                // todo : use origin ctd, al_kind_flag = 0
                versionList.add(0);
                return CtdService.initFitnessScore;
            } else if (index >= judgeFuncList.size() - 1) {
                // 排名在最后一名
                // todo : 以后不用罚函数了,直接套用representing learning, al_kind_flag = 1
                versionList.add(1);
                return CtdService.initFitnessScore;
            } else {

                versionList.add(1);
                // 罚函数的消融实验
                // return CtdService.initFitnessScore;

                // todo : 表示学习嵌入ctd，并且加上罚函数, al_kind_flag = 1
                rmseList = judgeFuncList;
                // attention : 罚函数计算, k
                if (rmseList.size() < k + 1) {
                    rmseList.add(RMSEScore);
                    if (RMSEScore < minRMSE) {
                        minRMSE = RMSEScore;
                    }
                } else {
                    // 保留TopK最小的
                    rmseList.add(RMSEScore);
                    rmseList.remove(Collections.max(rmseList));
                    rmseList.remove(0.0);
                    // 升序,rmse小的，返回的index小，评估的分数就小，适应度下降，不合理
//            Collections.sort(rmseList);
                    // 降序，rmse小的，index大，评估分数大，适应度高
                    Collections.reverse(rmseList);
                    Word2VecModel m = CtdService.getTriModel();
                    try {
                        int rank = rmseList.indexOf(RMSEScore);
                        if (rank < 0) {
                            exit(-200);
                        }
                        // fixme : debug k
                        double Qi = (double) (rank + 1) / (k + 1);
                        this.Qi = Qi;
                        double B_sum = 0;
                        for (int s1 = 1; s1 < sourceNum; s1++) {
                            for (int s2 = s1 + 1; s2 <= sourceNum; s2++) {
                                // s1与s2的weight
                                String sourceP = "source_" + s1;
                                String sourceQ = "source_" + s2;
                                // fixme : 适应度函数中不存在调用新的distance的部分
                                double detaSimilarity = Math.abs(distanceUseSavedModel(m, sourceP, sourceQ));
                                double detaWeight = 0;
                                try {
                                    detaWeight = Math.abs(weightList.get(s1) - weightList.get(s2));
                                } catch (IndexOutOfBoundsException e) {
                                    detaWeight = 0;
                                }
                                B_sum += Math.abs(detaSimilarity - detaWeight);
                            }
                        }
                        this.B_sum = B_sum;
                        if (String.valueOf((double) Qi * B_sum).equals("NaN") || B_sum == 0.0) {
                            exit(-50);
                            return 8.0 + CtdService.initFitnessScore;
                        }

                        fitScore = Qi * B_sum;
                        return fitScore + CtdService.initFitnessScore;
                    } catch (NullPointerException e) {
                        // 被抛弃了，排序很低
                        return 8.0 + CtdService.initFitnessScore;
                    }
                }
                exit(-20);
                return -20;
            }
        }
        return 0.0;
    }

    private List<String> initialFileListDA() {
        List<String> fileListDA = new ArrayList<>();
        for (int i = 0; i < sourceNum; i++) {
            int temp = i + 1;
//            String filePath = "data/ctd/monitor/sourceDA/source" + temp + ".csv";
            String filePath = dataPath + "/sourceDA/source" + temp + ".csv";
            fileListDA.add(filePath);
        }
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
        String truthFilePath = dataPath + "/threetruth.CSV";
        fileList.add(truthFilePath);

        return fileList;
    }

    public List<String> initialDC() {
        List<String> DCs = new ArrayList<>();
        // fixme : weather的否定约束
        DCs.add("day < 2");
        return DCs;
    }

    public String[][] readGoldStandard(int D1, int D2) {
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = dataPath + "/threetruth.csv";
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
                data = str.split(",", -1);
                System.arraycopy(data, 0, goldenStandard[row], 0, D2);
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
    public List<Double> errorForMonitor(String[][] calcTruth, String[][] goldenStandard, int D1, int D2) throws NullPointerException {
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
                    } else if (j == 100) {
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
                            for (String t_str : typeArray) {
                                // error < 5%
                                if ((Math.abs(Double.parseDouble(t_str) - Double.parseDouble(str)) < Double.parseDouble(t_str) * 0.05)) {
                                    precise += 1.0 / calcArray.length;
                                    // 命中了
                                    break;
                                }
                            }
                        }
                        sum += 1 - precise;
                        res += (1 - precise) * (1 - precise);
                        error_sample += 1 - precise;
                    } else if (j == 2 || j == 3) {
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
                        if (cha > 0.01) {
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
            System.out.println("error distance");
            exit(-3);
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

    public void initParameter() {
        File source = new File(dataPath + "/source/source1.csv");
        try {
            FileReader fileReader = new FileReader(source);
            BufferedReader br = new BufferedReader(fileReader);
            String str;
            String[] data;
            attrName = br.readLine();
            D2 = attrName.split(",", -1).length;
            int line = 0;
            while ((str = br.readLine()) != null) {
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
                while ((str = bufferedReader.readLine()) != null) {
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
            System.out.println("word not find");
            return 0;
        }

    }

    @Test
    public void test() throws IOException {
        GAImplTest gaImplTest = new GAImplTest();
        gaImplTest.calculate();
        // 最好的代数
        gaImplTest.getGeneI();

    }


}