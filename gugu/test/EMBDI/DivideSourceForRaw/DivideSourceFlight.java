package EMBDI.DivideSourceForRaw;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DivideSourceFlight {
    @Test
    public void divideSource(){
        // number of sources
        int sourceNum = 27;
        // number of tuple in one source
        int tupleNum = 10;
        String totalFile = "data/Flight/SampleFlight/sampled.csv";
        String str;
        try {
            FileReader fr = new FileReader(totalFile);
            BufferedReader br = new BufferedReader(fr);
            String temp;
            // throw first row, store attributes
            temp = br.readLine();
            int sourceSeq = 0;
            int lastSource = 0;
            int newSource = 0;
            String lastStrNotRead = null;
            for(int i = 0;i<sourceNum;i++){
                int seq = i+1;
                String filePath = "data/Flight/SampleFlight/source" + seq  + ".csv";
                File f = new File(filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(f);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                String[] sampleSeq = {"2","20","21","25","26","31","73","75","89","93"};
                try {
                    f.createNewFile();
                    // 输入属性，也可以从extract中获取
                    System.out.println(temp);
                    String[] data = null;
                    // System.out.println("source,sample,change%,last_trade_price,open_price,volumn,today_high,today_low,previous_close,52wk_H,52wk_L");
                    if(seq != 1) System.out.println(lastStrNotRead);
                    while ((str = br.readLine())!=null){
                        data = str.split(",");
                        newSource = Integer.parseInt(data[0]);
                        if(lastSource != newSource){
                            // source change, switch to next file
                            sourceSeq = newSource;
                            lastSource = newSource;
                            lastStrNotRead = str;
                            break;
                        }
                        sourceSeq = newSource;
                        lastSource = newSource;
                        System.out.println(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                printStream.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

