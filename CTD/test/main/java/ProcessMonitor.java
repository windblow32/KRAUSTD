package main.java;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Test
    public void source(){
        for(int i = 1;i<=5;i++){
            String sourcePath = "data/monitor0707/source/monitor-source"+i+".csv";
            File sourceFile = new File(sourcePath);
            File output = new File("data/monitor0707/source/source"+i+".csv");
            FileReader fr = null;
            try {
                fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                // delete first line
                br.readLine();
                PrintStream ps = new PrintStream(output);
                System.setOut(ps);
                System.out.println("entity,brand,screen_type,supported_aspect_ratio,response_time(ms),screen_size_diagonal,day");
                String str;
                String[] data;
                br.mark((int) (sourceFile.length()+1));
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("2",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }

                }
                br.reset();
                // da
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("1",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[1];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("3",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[1];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("4",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[1];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("5",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[1];
                        System.out.println(outputStr);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        /*
        判断后缀
         */

    }
    @Test
    public void sourceDA(){
        for(int i = 2;i<=5;i++){
            String sourcePath = "data/monitor0707/sourceDA/monitor-source"+i+".csv";
            File sourceFile = new File(sourcePath);
            File output = new File("data/monitor0707/sourceDA/source"+i+".csv");
            FileReader fr = null;
            try {
                fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                // delete first line
                br.readLine();
                PrintStream ps = new PrintStream(output);
                System.setOut(ps);
                System.out.println("entity,brand,screen_type,supported_aspect_ratio,response_time(ms),screen_size_diagonal,day");
                String str;
                String[] data;
                br.mark((int) (sourceFile.length()+1));
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("2",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }

                }
                br.reset();
                // da
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("1",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("3",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("4",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }
                }
                br.reset();
                // 其余
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("5",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6]+splitSgn+data[7]+splitSgn+data[8];
                        System.out.println(outputStr);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        /*
        判断后缀
         */

    }
    @Test
    public void allTruth(){
        String sourcePath = "data/camera0707/alltruth/camera_alltruth.csv";
        File sourceFile = new File(sourcePath);
        File output = new File("data/camera0707/alltruth/allTruth.CSV");
        FileReader fr = null;
        try {
            fr = new FileReader(sourceFile);
            BufferedReader br = new BufferedReader(fr);
            // delete first line
            br.readLine();
            PrintStream ps = new PrintStream(output);
            System.setOut(ps);
            System.out.println("entity,num,condition,day");
            String str;
            String[] data;
            br.mark((int) (sourceFile.length()+1));
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                String splitSgn = ",";
                // 标注数据
                if(judgeHZ("2",data[0])){
                    System.out.println(str);
                }

            }
            br.reset();
            // da
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                String splitSgn = ",";
                // 标注数据
                if(judgeHZ("1",data[0])){
                    System.out.println(str);
                }
            }
            br.reset();
            // 其余
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                String splitSgn = ",";
                // 标注数据
                if(judgeHZ("3",data[0])){
                    System.out.println(str);
                }
            }
            br.reset();
            // 其余
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                String splitSgn = ",";
                // 标注数据
                if(judgeHZ("4",data[0])){
                    System.out.println(str);
                }
            }
            br.reset();
            // 其余
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                String splitSgn = ",";
                // 标注数据
                if(judgeHZ("5",data[0])){
                    System.out.println(str);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean judgeHZ(String s,String str){
        String pattern = "^\\w*("+s+")$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
