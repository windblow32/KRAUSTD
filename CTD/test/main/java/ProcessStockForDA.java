package main.java;

import org.junit.Test;

import java.io.*;

public class ProcessStockForDA {
    public int sourceNum = 55;
    @Test
    public void processStock(){
        // 接收数据格式是顺序一定的
        File da = new File("data/stock100/divideSourceDA/true_15_30_tang.csv");
        try {
            FileReader fr = new FileReader(da);
            BufferedReader br = new BufferedReader(fr);

            String str;
            String[] data;
            for(int s = 0;s<sourceNum;s++){
                int t = s + 1;
                String sourceFilePath = "data/stock100/divideSourceDA/source" + t + ".csv";
                File sourceFile = new File(sourceFilePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                // print attr
                System.out.println("sample,change%,last_trade_price,open_price,volumn,today_high,today_low,previous_close,52wk_H,52wk_L");
                int line = 0;
                br.mark((int)da.length()+1);
                while((str = br.readLine())!=null&&line<23){
                    System.out.println(str);
                    line++;
                }
                int daLine = 0;
                while((str = br.readLine())!=null){
                    if(daLine%sourceNum==s){
                        System.out.println(str);
                    }
                    daLine++;
                }
                printStream.close();
                fileOutputStream.close();
                br.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
