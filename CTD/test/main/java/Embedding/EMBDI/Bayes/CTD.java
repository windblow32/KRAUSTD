package main.java.Embedding.EMBDI.Bayes;


import main.java.CTD_Algorithm;
import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class CTD {
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
    // 存储Top k
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = Double.MAX_VALUE;
    private String attrName;
    // string 多值
    public List<Integer> stringType_multi_list = new ArrayList<>();
    // num 多值
    public List<Integer> numType_multi_list = new ArrayList<>();
    public Map<String, float[]> fastText = new HashMap<>();
    public List<Integer> versionList = new ArrayList<>();
    public PrintStream stdout = System.out;
    public double fitness;

    @Test
    public void run() throws IOException {
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
        initParameter();
        initMultipleList();
        for(int version = 1;version<10;version++){
            if(version == 1){
                versionList.add(0);
            }
            if(existDA==1&&version==1){
                LocalTime time_pre = LocalTime.now();
                DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
                t_DApre = time_pre.format(formatter_pre);
                // add DA
                CTD_Algorithm DA_CTDService = new CTD_Algorithm();
                DA_CTDService.update(biaozhushu, zengqiangshu, dataPath, existDA, version, fileListDA, sourceNum, DCs, "THREE",
                        63,
                        5,
                        5,
                        0,
                        5,
                        4,
                        4,
                        1,
                        1,
                        0,
                        119,
                        4,
                        versionList,
                        1,
                        1,
                        fastText);
                // time end
                LocalTime time_after = LocalTime.now();
                t_DAafter = time_after.format(formatter_pre);
            }
            CTD_Algorithm CtdService = new CTD_Algorithm();
            String t_pre;
            String t_after;
            LocalTime time_pre = LocalTime.now();
            DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
            t_pre = time_pre.format(formatter_pre);

            Process proc;
            try {
                proc = Runtime.getRuntime().exec("python E:\\GitHub\\KRAUSTD\\CTD\\bayesian_optimization.py");// 执行py文件
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
            // attention : read output from bayes
            File parameter = new File("E:\\GitHub\\pythonProject\\log\\para_bayes\\output.txt");
            BufferedReader br = new BufferedReader(new FileReader(parameter));
            String str;
            List<Integer> paraList = new ArrayList<>();
            while((str = br.readLine())!=null){
                int num = (int)Double.parseDouble(str);
                if(num<=0.0f){
                    paraList.add(0);
                }else paraList.add((int)Double.parseDouble(str));
            }
            br.close();
            int length = paraList.get(0);
            int AttrDistributeLow = paraList.get(1);
            int AttrDistributeHigh = paraList.get(2);
            int ValueDistributeLow = paraList.get(3);
            int ValueDistributeHigh = paraList.get(4);
            int TupleDistributeLow = paraList.get(5);
            int TupleDistributeHigh = paraList.get(6);
            int dropSourceEdge = paraList.get(7);
            int dropSampleEdge = paraList.get(8);
            int isCBOW = paraList.get(9);
            int dim = paraList.get(10);
            int windowSize = paraList.get(11);

            // fixme : only ctd把最后一个参数设置为0
            weightList = CtdService.update(biaozhushu, zengqiangshu, dataPath, existDA, version, fileList, sourceNum + 1, DCs, "THREE",
                    paraList.get(0),
                    paraList.get(1),
                    paraList.get(2),
                    paraList.get(3),
                    paraList.get(4),
                    paraList.get(5),
                    paraList.get(6),
                    paraList.get(7),
                    paraList.get(8),
                    paraList.get(9),
                    paraList.get(10),
                    paraList.get(11),
                    versionList,0, 1,
                    fastText);
            // time end
            LocalTime time_after = LocalTime.now();
            formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
            t_after = time_after.format(formatter_pre);

            String[][] calcTruth = CtdService.getCalcTruth();
            // attention : input for bayes
            fitness = CtdService.initFitnessScore;
            File output = new File("E:\\GitHub\\pythonProject\\log\\para_bayes\\input.txt");
            PrintStream ps = new PrintStream(output);
            System.setOut(ps);
            System.out.println(fitness);
            System.setOut(stdout);

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
            error_list = errorCalculate(calcTruth, goldenStandard, D1, D2);
            // attention : error list
            double RMSEScore = error_list.get(1);
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
                PrintStream logps = new PrintStream(fos);
                System.setOut(logps);
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
            System.setOut(stdout);
        }

    }
    public List<Double> errorCalculate(String[][] calcTruth, String[][] goldenStandard, int D1, int D2) throws NullPointerException {
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
}

