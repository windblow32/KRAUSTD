package EMBDI;

import org.junit.Test;

import java.io.*;

public class DataProcessFlight {
    @Test
    public void process(){
        //Fixme
        String file = "data/Flight/originFlight/flight_data.csv";
        try {
            // fixme
            File f = new File("data/Flight/SampleFlight/sampled.csv");
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
                // sample, schedule = 0, sample seq chosen according to truthFile(first 10)
                if(data[2].equals("0")){
                    if(data[1].equals("2")||data[1].equals("20")
                            ||data[1].equals("21")||data[1].equals("25")
                            ||data[1].equals("26")||data[1].equals("31")
                            ||data[1].equals("73")||data[1].equals("75")
                            ||data[1].equals("89")||data[1].equals("93")){
                        System.out.println(str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
