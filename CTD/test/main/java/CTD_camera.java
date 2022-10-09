package main.java;

import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CTD_camera {
    // todo : 数据集路径
    public String dataPath = "data/camera0707";
    // todo : 更换source中元组个数
    public int biaozhushu = 100;
    // todo : 数据源个数
    public int sourceNum = 6;
    public String[][] calcTruth = null;
    /*
    初始化数据集
     */
    public List<String> initFileList(String dataPath){
        List<String> fileList = new ArrayList<>();
        for(int i = 1;i<=sourceNum;i++){
            fileList.add(dataPath + "/source/source" + i + ".csv");
        }
        return fileList;
    }
    /*
    初始化否定约束
     */
    public List<String> initialDC(){
        List<String> DCs = new ArrayList<>();
        // todo : 否定约束
        DCs.add("day < 2");
        return DCs;
    }
    /*
    运行
     */
    @Test
    public void example(){
        List<String> fileList = initFileList(dataPath);
        List<String> DCList = initialDC();
        CTD_Algorithm CTDService = new CTD_Algorithm();
        CTDService.update(biaozhushu,0,dataPath,0,5,fileList,sourceNum,DCList,"THREE",30,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                0);
        String[][] calcTruth =  CTDService.getCalcTruth();
        this.calcTruth = calcTruth;
        // calcTruth和真值求RMSE
        // golden standard读取
        int D1 = CTDService.getD1();
        int D2 = CTDService.getD2();
        String[][] goldenStandard = readGoldStandard(D1,D2);
        // CTD
//        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        // monitor
        List<Double> error_list = new ArrayList<>();
        error_list = errorForMonitor(calcTruth,goldenStandard,D1,D2);
        LocalTime time1 = LocalTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String t1 = time1.format(formatter1);
        String[] data1 = t1.split(":");
        String insertT1 = data1[0] + data1[1] + data1[2];
        // todo : log路径
        String logPath = "log/Tri/CTD/weather/parameter/log" + insertT1 + ".txt";
        File logFile = new File(logPath);

        try {
            logFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);

            System.out.println("error rate : " + error_list.get(0));
            System.out.println("数值类型rmse: " + error_list.get(2));
            System.out.println("rmse total: " + error_list.get(1));
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    读取真值并比较
     */
    public String[][] readGoldStandard(int D1, int D2){
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = dataPath + "/allTruth.CSV";
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
    计算error
     */
    public List<Double> errorForMonitor(String[][] calcTruth, String[][] goldenStandard,int D1,int D2) throws NullPointerException {
        List<Double> error_list = new ArrayList<>();
        double r = 0;
        int n = 0;
        int error_sample = 0;
        double error_rate = 0;
        // 数字类型计算差值，string类型看是否一样,计算累计误差
        double sum = 0;
        // todo : 多值列的下标
        try {
            for (int i = 0; i < D1; i++) {
                for (int j = 2; j < D2; j++) {

                    if (calcTruth[i][j] == null
                            || goldenStandard[i][j] == null
                            || calcTruth[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("NaN")
                            || calcTruth[i][j].equals("")
                            || goldenStandard[i][j].equals("")
                    ) {
                        sum += 1;
                        error_sample++;
                    } else if (j == 100) {
                        // todo : num,并且有多值
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
                        error_sample += 1 - precise;
                    } else if (j == 3) {
                        // todo : String类型,并且有多值
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
                        error_sample += 1 - precise;

                    } else {
                        // 连续性数据
                        String str1 = calcTruth[i][j];
                        String str2 = goldenStandard[i][j];
                        double v1 = Double.parseDouble(str1);
                        double v2 = Double.parseDouble(str2);
                        double cha = Math.abs(v1 - v2);
                        if (cha > 2) {
                            // same
                            error_sample++;
                        }
                        r += cha * cha;
                        n++;
                        sum += cha;
                    }
                }
            }
        } catch (NullPointerException e3) {
            System.out.println("error exception");
        }
        error_rate = 1.0 * error_sample / (D1 * D2);
        double rmse = Math.sqrt((r * 1.0) / n);
        // 第一维
        error_list.add(error_rate);
        error_list.add(sum);
        error_list.add(rmse);

        return error_list;
    }

}
