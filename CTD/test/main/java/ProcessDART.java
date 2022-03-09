package main.java;

import org.junit.Test;

import java.io.*;

public class ProcessDART {
    @Test
    public void divideSource(){
        String totalFilePath = "data/dart/bookdata.csv";
        File f1 = new File(totalFilePath);
        int sourceNum = 5;
        int tupleNum = 12;
        String str;
        String[] data;
        // read origin data
        try {
            FileReader allfr = new FileReader(totalFilePath);
            BufferedReader allbr = new BufferedReader(allfr);
            String attr;
            // delete attr
            attr = allbr.readLine();
            allbr.mark((int)f1.length());
            for(int s = 1; s<= sourceNum; s++){
                String filePath = "data/dart/source"+s+".csv";
                File f = new File(filePath);
                try {
                    f.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    PrintStream printStream = new PrintStream(fileOutputStream);
                    System.setOut(printStream);
                    // add attr;
                    System.out.println(attr);
                    int line = 0;
                    allbr.reset();
                    while(line<200&&(str = allbr.readLine())!=null){
                        data = str.split(",",-1);
                        if(data[2].equals("dangdang")&&s==1){
                            System.out.println(str);
                        }
                        if(data[2].equals("xinhua")&&s==2){
                            System.out.println(str);
                        }
                        if(data[2].equals("bookuu")&&s==3){
                            System.out.println(str);
                        }
                        if(data[2].equals("yueyue")&&s==4){
                            System.out.println(str);
                        }if(data[2].equals("jiangtu")&&s==5){
                            System.out.println(str);
                        }

                        line++;
                    }
                    printStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            allfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void divideStockForIATD(){
        int sourceNum = 55;
        int sampleNum = 100;
        String filePath = "data/iatd/stockForIATD.CSV";
        File f = new File(filePath);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            br.readLine();
            br.mark((int)f.length());

            for(int s = 1;s<=sourceNum;s++){
                int t = s - 1;

                String sourcePath = "data/iatd/source_"+t+".csv";
                File sf = new File(sourcePath);
                sf.createNewFile();
                PrintStream ps = new PrintStream(sf);
                System.setOut(ps);
                System.out.println("entity,domain");
                br.reset();
                while((str = br.readLine())!=null){
                    data = str.split(",", -1);
                    if(data[0].equals(String.valueOf(s))){
                        System.out.println(data[1]+","+data[2]);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
