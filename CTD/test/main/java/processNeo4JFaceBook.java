package main.java;

import java.io.*;

public class processNeo4JFaceBook {
    public void processFacebookData(){
        File f = new File("D:\\迅雷下载\\facebook\\0.featnames");
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(" ", -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
