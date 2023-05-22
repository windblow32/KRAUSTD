package main.java.Embedding.EMBDI.GA.preExper;

import main.java.CTD_Algorithm;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HyperParameter {
    public String dataPath = "data/monitor0707";
    public int existDA = 1;
    // fixme : change source 标注数
    public int biaozhushu = 20;
    public int zengqiangshu = 20;
    // fixme : change source 数据源
    public int sourceNum = 5;
    public String attr;
    public PrintStream out = System.out;
    private int D2;
    private String[][] calcTruth;
    private int D1;
    private double rmse = 0;

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

    @Test
    public void test() {

        List<String> fileList;
        // fixme : 更换整体数据集时变化 ，dataset type
        fileList = initialFileList();
        List<String> DCs;
        DCs = initialDC();

        List<Integer> versionList = new ArrayList<>();
        versionList.add(0);
        attr = "windowSize";
        int value = 5;
        for(int i = 2;i<12;i++) {
            List<Double> weightList = new ArrayList<>();
            CTD_Algorithm CtdService = new CTD_Algorithm();
            weightList = CtdService.update(biaozhushu, zengqiangshu, dataPath, existDA, 0, fileList, sourceNum + 1, DCs, "THREE",
                    63,
                    5,
                    7,
                    0,
                    5,
                    4,
                    4,
                    1,
                    1,
                    0,
                    119,
                    i,
                    versionList, 0, 1,
                    null);
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
            List<Double> error_list = errorForMonitor(calcTruth, goldenStandard, D1, D2);
            // attention : error list
            rmse = error_list.get(1);
            File log = new File("log/Tri/CTD/monitor/parameter/Ablation study/log_" + attr + i + ".txt");
            try {
                PrintStream ps = new PrintStream(log);
                System.setOut(ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("数据源权重" + weightList);
            System.out.println("参数 " + attr);
            System.out.println("value " + i);
            System.out.println("best value " + value);
            System.out.println("error rmse : " + error_list.get(2));
            System.out.println("error rate : " + error_list.get(0));
            System.out.println("error distance: " + error_list.get(1));
            System.out.println("适应度: " + CtdService.initFitnessScore);
            System.out.println("------------------------------");
            System.setOut(out);
        }

    }
}
