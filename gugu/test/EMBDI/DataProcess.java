package EMBDI;

import org.junit.Test;

import java.io.*;

public class DataProcess {
    @Test
    public void process(){
        String file = "data/originStock/alldataset_stock.csv";
        try {
            File f = new File("data/generateSample/test1.txt");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            FileReader fd = new FileReader(file);
            BufferedReader br = new BufferedReader(fd);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",");
                // sample(data[1]=112), day(data[18]=14)
                if(data[18].equals("14")){
                    if(data[1].equals("112")||data[1].equals("171")
                            ||data[1].equals("201")||data[1].equals("216")
                            ||data[1].equals("383")||data[1].equals("464")
                            ||data[1].equals("500")||data[1].equals("529")
                            ||data[1].equals("838")||data[1].equals("989")){
                        System.out.println(str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
