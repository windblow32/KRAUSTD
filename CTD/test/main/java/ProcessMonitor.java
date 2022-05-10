package main.java;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessMonitor {
    public int sourceNum = 5;
    @Test
    public void processOrigin(){
        String monitorPath = "data/monitor/monitor_original.csv";
        File f = new File(monitorPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()-1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/dart/monitor/source/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println(attr);
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[1])==i){
                        System.out.println(str);
                    }
                }
                br.reset();
                printStream.close();
                fileOutputStream.close();
            }
            br.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processDA_origin(){
        String monitorPath = "data/monitor/monitor_da.csv";
        File f = new File(monitorPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()-1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/dart/monitor/sourceDA/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println(attr);
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[1])==i){
                        System.out.println(str);
                    }
                }
                br.reset();
                printStream.close();
                fileOutputStream.close();
            }
            br.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processDA(){
        // entity
        List<String> daEntity = new ArrayList<String>();
        // 有的实体甚至不在所有+=、数据集中存在
        daEntity.add("22");
        daEntity.add("25");
        daEntity.add("27");
        daEntity.add("28");
        daEntity.add("193");

        String monitorPath = "data/monitor/monitor_da.csv";
        File f = new File(monitorPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()-1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/dart/monitor/sourceDA/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println(attr);
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[1])==i){
                        if(daEntity.contains(data[0])){
                            System.out.println(str);
                        }
                    }
                }
                br.reset();
                printStream.close();
                fileOutputStream.close();
            }
            br.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
