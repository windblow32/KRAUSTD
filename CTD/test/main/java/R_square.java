package main.java;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class R_square {

    // modify
    public int D1 = 100;
    public int D2 = 10;
    // 用了图表示学习的真值结果
    public String resultPath = "data/stock100/result/result_9_1.csv";

    @Test
    public void test(){
        String[][] truth =  readGoldStandard(D1,D2);
        String[][] calcTruth = readResult(D1,D2,resultPath);
        double rmse = RMSE(calcTruth, truth, D1, D2);
        double r_square = R_square(calcTruth, truth, D1, D2);
        System.out.println("rmse : " + rmse);
        System.out.println("r_square : " + r_square);
        System.out.println("****************************");
        // 原始CTD的结果
        String calcResultCTDOnlyPath = "data/stock/stock100/result/result_1_1.csv";
        String[][] calcTruthOnlyCTD = readResult(D1,D2,calcResultCTDOnlyPath);
        double rmseOnlyCTD = RMSE(calcTruthOnlyCTD, truth, D1, D2);
        double r_squareOnlyCTD = R_square(calcTruthOnlyCTD, truth, D1, D2);
        System.out.println("rmseOnlyCTD : " + rmseOnlyCTD);
        System.out.println("r_squareOnlyCTD : " + r_squareOnlyCTD);
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
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goldenStandard;

    }

    public String[][] readResult(int D1, int D2, String resultPath){
        String[][] result = new String[D1][D2];
        try {

            FileReader fr = new FileReader(resultPath);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            int row = 0;
            // 扔掉第一行attr
            br.readLine();
            while ((str = br.readLine())!=null){
                // data长度不足
                data = str.split(",",-1);
                System.arraycopy(data,0,result[row],0,D2);
                row++;
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

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
                        continue;
                    }
                    double v2 = Double.parseDouble(goldenStandard[i][j]);
                    golden_standard_sum += v2;
                }catch (NumberFormatException | NullPointerException e) {
                    // fixme 异常处理为0是否合理
                    golden_standard_sum += 0;
                }

            }
        }
        golden_standard_sum = golden_standard_sum/(100*10);
        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                try{
                    if(goldenStandard[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("")
                            || goldenStandard[i][j] == null){
                        sum2 += 0;
                        continue;
                    }
                    double v2 = Double.parseDouble(goldenStandard[i][j]);
                    sum2 += (double) Math.pow((v2-golden_standard_sum),2);
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
                        continue;
                    }
                    double v1 = Double.parseDouble(calcTruth[i][j]);
                    double v2 = Double.parseDouble(goldenStandard[i][j]);
                    sum1 += Math.pow(v1-v2,2);
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
                        continue;
                    }
                    double v1 = Double.parseDouble(calcTruth[i][j]);
                    double v2 = Double.parseDouble(goldenStandard[i][j]);
                    sum += Math.pow(Math.abs(v1-v2),2);
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
}
