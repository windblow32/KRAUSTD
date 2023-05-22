package main.java.Embedding.EMBDI.Bayes;


import com.google.common.collect.ImmutableList;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.DART;
import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class DART_Bayes {
    public String dataPath = "data/monitor0707";
    public int existDA = 1;
    // fixme : change source 标注数
    public int biaozhushu = 20;
    public int zengqiangshu = 20;
    public List<String> daTupleList = new ArrayList<>();
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
    // string 多值
    public List<Integer> stringType_multi_list = new ArrayList<>();
    // num 多值
    public List<Integer> numType_multi_list = new ArrayList<>();
    public Map<String, float[]> fastText = new HashMap<>();
    public List<Integer> versionList = new ArrayList<>();
    public PrintStream stdout = System.out;
    public double fitness;
    public int isDA;
    public Word2VecModel DARTModel;
    private String attrName;

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

    @Test
    public void run() throws IOException {
        // 在此处删除可能存在的之前构造的图结构文件，避免印象模型训练
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        deleteWithPath(graphPath);
        String t_DApre = null;
        String t_DAafter = null;
        initParameter();
        initMultipleList();
        if (existDA == 1) {
            // DAfileList = initialFileListDA();
            String daSet = dataPath + "/sourceDA/source1.csv";
            daTupleList = initDATupleList(daSet);
        }
        for (int version = 1; version < 10; version++) {
            if (version == 1) {
                versionList.add(0);
            }

            if (existDA == 1 && version == 1) {
                LocalTime time_pre = LocalTime.now();
                DateTimeFormatter formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
                t_DApre = time_pre.format(formatter_pre);
                // add DA
                isDA = 1;
                DART dart = new DART();
                dart.sourceNum = sourceNum;
                dart.dataPath = dataPath;
                dart.distanceForDartUsingMonitorDA(
                        63,
                        20000,
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
                        String.valueOf(version));
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
            while ((str = br.readLine()) != null) {
                int num = (int) Double.parseDouble(str);
                if (num <= 0.0f) {
                    paraList.add(0);
                } else paraList.add((int) Double.parseDouble(str));
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

            // dart
            String calcTruthPath = "E:\\GitHub\\KRAUSTD\\dart\\" + version + "_truth.csv";
            // calc tempDA
            if (existDA == 1) {
                getTempDA();
            }
            DART dart = new DART();
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
                    windowSize, String.valueOf(version));
            // time end
            LocalTime time_after = LocalTime.now();
            formatter_pre = DateTimeFormatter.ofPattern("HH:mm:ss");
            t_after = time_after.format(formatter_pre);

            String[][] calcTruth = readCalcTruth(D1, D2, calcTruthPath);
            this.calcTruth = calcTruth;
            // calcTruth和真值求RMSE
            // golden standard读取

            String[][] goldenStandard = readGoldStandard(D1, D2);
            // 遗传算法的评分
            double score = calcInitFitnessScore(version);
            // CTD
//        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
            // monitor的error rate
            List<Double> error_list = new ArrayList<>();
            // num multi index list and string multi index list

            error_list = errorCalculate(calcTruth, goldenStandard, D1, D2);
            double RMSEScore = error_list.get(1);        // monitor 中就是error rate
            // using CTD print last time's extracted data's rmse
//        extractedCTD_RMSE = CtdService.getRmseForGA();
            // delete calc file
            deleteWithPath("E:\\GitHub\\KRAUSTD\\dart\\DA" + 1 + "_truth.csv");
            deleteWithPath("E:\\GitHub\\KRAUSTD\\dart\\" + version + "_truth.csv");
            // set printStream and rmse output filePath="log/Tri/weightCalcByVex/parameter/1.txt"
            LocalTime time1 = LocalTime.now();
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
            String t1 = time1.format(formatter1);
            String[] data1 = t1.split(":");
            String insertT1 = data1[0] + data1[1] + data1[2];
            // attention : log path
            String logPath;
            logPath = "log/Tri/DART/monitor/parameter/log" + insertT1 + ".txt";
            // attention : input for bayes
            File output = new File("E:\\GitHub\\pythonProject\\log\\para_bayes\\input.txt");
            PrintStream ps = new PrintStream(output);
            System.setOut(ps);
            System.out.println(score);
            System.setOut(stdout);
            this.calcTruth = calcTruth;
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
                System.out.println("error rmse : " + error_list.get(2));
                System.out.println("error rate : " + error_list.get(0));
                System.out.println("error distance: " + error_list.get(1));
                System.out.println("适应度: " + score);
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

    private void initMultipleList() {
        numType_multi_list.clear();
        stringType_multi_list.clear();
        if (dataPath.equals("data/monitor0707")) {
            numType_multi_list.add(100);
            stringType_multi_list.add(2);
            stringType_multi_list.add(3);
        } else if (dataPath.equals("data/camera0707")) {
            numType_multi_list.add(100);
            stringType_multi_list.add(3);
        } else {
            numType_multi_list.add(100);
            for (int i = 0; i < D2; i++) {
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


    // 计算初始适应度，内部包含global embedding生成
    public double calcInitFitnessScore(int times) {
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

    private List<Float> double2float(ImmutableList<Double> temp){
        List<Float> newList = new ArrayList<>();
        for(double n : temp){
            newList.add((float)n);
        }
        return newList;
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

}

