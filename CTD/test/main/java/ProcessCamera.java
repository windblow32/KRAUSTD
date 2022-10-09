package main.java;

import org.junit.Test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessCamera {
    @Test
    public void divideSource(){
        String totalFilePath = "data/iatd/camera/camera_da.csv";
        File f1 = new File(totalFilePath);
        int sourceNum = 6;
        int tupleNum = 20;
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
                String filePath = "data/iatd/camera/sourceDA/source"+s+".csv";
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
                    while(line<80&&(str = allbr.readLine())!=null){
                        data = str.split(",",-1);
                        if(data[1].equals("1")&&s==1){
                            System.out.println(str);
                        }
                        if(data[1].equals("2")&&s==2){
                            System.out.println(str);
                        }
                        if(data[1].equals("3")&&s==3){
                            System.out.println(str);
                        }
                        if(data[1].equals("4")&&s==4){
                            System.out.println(str);
                        }
                        if(data[1].equals("5")&&s==5){
                            System.out.println(str);
                        }
                        if(data[1].equals("6")&&s==6){
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
    /*
    step 1
     */
    @Test
    public void source(){
        for(int i = 1;i<=6;i++){
            String sourcePath = "data/camera0707/source/camera-source"+i+".csv";
            File sourceFile = new File(sourcePath);
            File output = new File("data/camera0707/source/source"+i+".csv");
            FileReader fr = null;
            try {
                fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                // delete first line
                br.readLine();
                PrintStream ps = new PrintStream(output);
                System.setOut(ps);
                System.out.println("entity,brand,num,condition,day");
                String str;
                String[] data;
                br.mark((int) (sourceFile.length()+1));
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("2",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
        for(int i = 2;i<=6;i++){
            String sourcePath = "data/camera0707/sourceDA/camera-source"+i+".csv";
            File sourceFile = new File(sourcePath);
            File output = new File("data/camera0707/sourceDA/source"+i+".csv");
            FileReader fr = null;
            try {
                fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                // delete first line
                br.readLine();
                PrintStream ps = new PrintStream(output);
                System.setOut(ps);
                System.out.println("entity,brand,num,condition,day");
                String str;
                String[] data;
                br.mark((int) (sourceFile.length()+1));
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    String splitSgn = ",";
                    // 标注数据
                    if(judgeHZ("2",data[0])){
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
                        String outputStr = data[0]+splitSgn+data[2]+splitSgn+data[4]+splitSgn+data[5]+splitSgn+data[6];
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
    @Test
    public void judge2(){
        String s = "1";
        String str = "13_1";
        String pattern = "^\\w*("+s+")$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        boolean b = m.matches();
        System.out.println(b);
    }

}
