package main.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dataprocess {
    private final int filenum = 36;
    public void generate(String File){
        FileReader fd = null;
        try {
            fd = new FileReader(File);
            BufferedReader br = new BufferedReader(fd);
            String str;
            while((str=br.readLine())!=null){
                String[] data = str.split(",");
                // data[0] stores the source of data
                int source = Integer.parseInt(data[0]);
                String filename = "Flight " + source;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
