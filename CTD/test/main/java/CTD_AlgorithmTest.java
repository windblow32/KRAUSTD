package main.java;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CTD_AlgorithmTest {
    public int sourceNum = 55;
    private String[][] calcTruth = null;
    private int D1;
    private int D2;

    @Test
    public void CTD_BRM(){
        int t = 3;
        for(int i = 0;i<t;i++){
            testUpdate();
        }
    }

    public void testUpdate() {
        List<String> fileList = new ArrayList<>();
        fileList = initialFileList();
        List<String> DCs = new ArrayList<>();
        DCs = initialDC();
        CTD_Algorithm CtdService = new CTD_Algorithm();

        CtdService.update(1,fileList,sourceNum,DCs,"THREE",60,4,
                4,
                4,
                4,
                4,
                4,
                1,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                0,0,1);
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
        // 输出rmseScore
        LocalTime time1 = LocalTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String t1 = time1.format(formatter1);
        String[] data1 = t1.split(":");
        String insertT1 = data1[0] + data1[1] + data1[2];
        String fPath = "log/Tri/CTD/brm/ctd-brm-rmse " + insertT1 + ".txt";
        File f = new File(fPath);
        try {
            f.createNewFile();
            PrintStream ps = new PrintStream(f);
            System.setOut(ps);
            System.out.println(RMSEScore);
            ps.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public List<String> initialFileList(){
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
}