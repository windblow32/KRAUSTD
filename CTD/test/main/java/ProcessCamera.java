package main.java;

import org.junit.Test;

import java.io.*;

public class ProcessCamera {
    @Test
    public void divideSource(){
        String totalFilePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\camera\\camera_5_28_140.csv";
        File f1 = new File(totalFilePath);
        int sourceNum = 5;
        int tupleNum = 28;
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
                String filePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\camera\\source"+s+".csv";
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
                    while(line<140&&(str = allbr.readLine())!=null){
                        data = str.split(",",-1);
                        if(data[1].equals("www.shopmania.in")&&s==1){
                            System.out.println(str);
                        }
                        if(data[1].equals("www.ebay.com")&&s==2){
                            System.out.println(str);
                        }
                        if(data[1].equals("www.pricedekho.com")&&s==3){
                            System.out.println(str);
                        }
                        if(data[1].equals("www.gosale.com")&&s==4){
                            System.out.println(str);
                        }
                        if(data[1].equals("www.mypriceindia.com")&&s==5){
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
}
