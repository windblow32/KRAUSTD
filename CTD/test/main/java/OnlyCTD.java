package main.java;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnlyCTD {
    public List<String> initialDC(){
        List<String> DCs = new ArrayList<>();
        DCs.add("screen_size_diagonal < 100");
        return DCs;
    }
    private List<String> initialFileListDA() {
        List<String> fileListDA = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int temp = i + 1;
            String filePath = "data/ctd/monitor/sourceDA/source" + temp + ".csv";
            fileListDA.add(filePath);
        }
        // fixme : 真值添加
        String truthFilePath = "data/ctd/monitor/monitor_truth_da.CSV";
        fileListDA.add(truthFilePath);
        return fileListDA;
    }
    public List<String> initialFileList(String dataset){
        List<String> fileList = new ArrayList<>();
        if(dataset.equals("CTD")){
            for (int i = 0; i < 55; i++) {
                int temp = i + 1;
                String filePath = "data/stock100/divideSource/source" + temp + ".csv";
                fileList.add(filePath);
            }
            // fixme : 真值添加
            String truthFilePath = "data/stock100/100truth.csv";
            fileList.add(truthFilePath);
        }else if(dataset.equals("monitor")){
            for (int i = 1; i <= 5; i++) {
                String filePath = "data/ctd/monitor/source/source" + i + ".csv";
                fileList.add(filePath);
            }
            // fixme : 真值添加
            String truthFilePath = "data/ctd/monitor/monitor_truth.csv";
            fileList.add(truthFilePath);
        }

        return fileList;
    }
    @Test
    public void testOnlyCtd(){
        String dataset = "monitor";
        List<String> fileList = new ArrayList<>();
        fileList = initialFileList(dataset);
        List<String> DCs = new ArrayList<>();
        DCs = initialDC();
        List<String> fileListDA = new ArrayList<>();
        fileListDA = initialFileListDA();
        CTD_Algorithm CtdService = new CTD_Algorithm();
        CtdService.update(1,fileList,5,DCs,"THREE",60,4,
                4,
                4,
                4,
                4,
                4,
                4,
                4,
                0,
                0,
                0,
                0,
                1,
                100,
                1,
                0,
                0);
        String[][] calcTruth =  CtdService.getCalcTruth();
        // calcTruth和真值求RMSE
        // golden standard读取
        int D1 = CtdService.getD1();
        int D2 = CtdService.getD2();
        String[][] goldenStandard = readGoldStandard(D1,D2);
        double RMSEScore = errorForMonitor(calcTruth,goldenStandard,D1,D2);
        System.out.println(RMSEScore);
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

    public double errorForMonitor(String[][] calcTruth, String[][] goldenStandard,int D1,int D2){
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
                    continue;
                }
                if(j==1){
                    // String类型,并且有多值
                    String type = calcTruth[i][j];
                    String truthType = goldenStandard[i][j];
                    String[] typeArray;
                    typeArray = truthType.split(";");
                    double minDis = 0;
                    for(String str : typeArray){
                        double currentDis = Levenshtein(type,str);
                        if(currentDis < minDis){
                            minDis = currentDis;
                        }
                    }
                    sum += minDis;
                }else if(j == 2){
                    String type = calcTruth[i][j];
                    String truthType = goldenStandard[i][j];
                    sum += Levenshtein(type,truthType);
                }else{
                    // 连续性数据
                    String str1 = calcTruth[i][j];
                    String str2 = goldenStandard[i][j];
                    double v1 = Double.parseDouble(str1);
                    double v2 = Double.parseDouble(str2);
                    sum += Math.abs(v1-v2);
                }
            }
        }

        return sum;
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

}
