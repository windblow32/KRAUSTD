package EMBDI;

import org.junit.Test;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class DataProcess {
    @Test
    public void process() {
        String file = "data/stock100/100truth.csv";
        try {
            File f = new File("data/stock100/100sourceData.csv");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            FileReader fd = new FileReader(file);
            BufferedReader br = new BufferedReader(fd);
            String str;
            String[] data;
            List<String> sampleList = new ArrayList<>();
            // from truth get sampleList
            while ((str = br.readLine()) != null) {
                data = str.split(",");
                // sample(data[1]=112), day(data[18]=14)
//                if (data[18].equals("17")) {
//                    if (data[1].equals("4") || data[1].equals("27")
//                            || data[1].equals("29") || data[1].equals("37")
//                            || data[1].equals("383") || data[1].equals("464")
//                            || data[1].equals("500") || data[1].equals("529")
//                            || data[1].equals("838") || data[1].equals("989")) {
//                        System.out.println(str);
//                    }
//                }
                sampleList.add(data[0]);
            }
            String sourceData = "data/originStock/alldataset_stock.csv";
            FileReader newFd = new FileReader(sourceData);
            BufferedReader newBr = new BufferedReader(newFd);
            String[] newData;
            String newStr;
            // use sampleList choose sample from sourceDATA
            while((newStr=newBr.readLine())!=null){
                newData = newStr.split(",");
                if(newData[18].equals("17")){
                    if(sampleList.contains(newData[1])){
                        System.out.println(newStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
