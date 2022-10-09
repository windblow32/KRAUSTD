package main.java;

import org.junit.Test;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class ProcessWeather {
    public int sourceNum = 15;
    @Test
    public void biaozhuAndDuiqi(){
        cutBiaoZhu();
        duiqi_entity();
    }
    /*
    第四步，在temp中生成对齐的标注数据,运行前真值需要只留下标注的，格式参考第二个
     */
    @Test
    public void duiqi_entity(){
        int num = 1000;
        // 对origin-weather_xxx文件分割后的数据集，实体对齐，id和threetruth一致
        File threetruth = new File("data/new_weather/total/allTruth.csv");
        Map<String,String> idMap = new HashMap<>();
        List<String> idList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(threetruth);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            br.readLine();
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                idMap.put(data[0],str);
                idList.add(data[0]);
            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String sourcePath = "";
        for(int i = 1;i<=sourceNum;i++){
            // fixme
            sourcePath = "data/new_weather/total/sourceDA/source"+i+".csv";
            File sourceFile = new File(sourcePath);
            String str;
            String[] data;
            String lastStr = "";
            try {
                FileReader fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                String attr = br.readLine();
                // fixme
                File newSource = new File("data/new_weather/total/temp/source"+i+".csv");
                PrintStream ps = new PrintStream(newSource);
                System.setOut(ps);
                System.out.println(attr);

                int line = 0;
                while ((str = br.readLine())!=null){
                    if (line >= num-1){
                        break;
                    }
                    lastStr = str;
                    data = str.split(",",-1);
                    if(line < num&&data[0].equals(idList.get(line))){
                        System.out.println(str);
                        line++;
                    }else {
                        // 打印真值的
                        // threetruth元组数
                        while(line < num&&(!data[0].equals(idList.get(line)))){
                            System.out.println(idMap.get(idList.get(line)));
                            line++;
                        }
                        line++;
                        if(line<num){
                            System.out.println(str);
                        }
                    }
                }
                // 只能修复到每个source的最后一个元组
                if (line < num) {
                    System.out.println(idMap.get(idList.get(line)));
                    line++;
                }
                // 最后结尾不是9584的补一个
//                data = lastStr.split(",",-1);
//                if(!data[0].equals("9584")){
//                    System.out.println("9584,Milwaukee,Mist/Fog,85,1");
//                }
                fr.close();
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*
    第三步，切分未标注需要把后面的增强实体删除
     */
    @Test
    public void cutBiaoZhu(){
        // 分割数据源
        String dataPath = "data/new_weather/weibiaozhu/origianl-weathe30,50,35.0.csv";
        File f = new File(dataPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            // String attr = br.readLine();
            br.mark((int)f.length()+1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/new_weather/30,50,35.0/sourceNew/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println("entity,Domain,Conditions,Humidity,day");
                List<String> entity = new ArrayList<>();
                int outputLines = 0;
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[0])==i&&(!(entity.contains(data[1])))){
                        entity.add(data[1]);
                        outputLines++;
                        StringBuilder s = new StringBuilder();
                        for(int k = 1;k< data.length;k++){
                            s.append(data[k]);
                            if(k < data.length - 1)
                                s.append(",");
                        }
                        System.out.println(s);

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

    /*
    第二步，处理neworigin，分割未增强数据sourceNew,已经是对齐的,需要先处理before da中的文件格式
     */
    @Test
    public void cutWeiZengQiang(){
        // 分割数据源
        String dataPath = "data/new_weather/origin/before da/neworigianl-weathe30,50,35.0.csv";
        File f = new File(dataPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()+1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/new_weather/30,50,35.0/weizengqiang/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println("entity,Domain,Conditions,Humidity,day");
                List<String> entity = new ArrayList<>();
                int outputLines = 0;
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[0])==i&&(!(entity.contains(data[1])))){
                        entity.add(data[1]);
                        outputLines++;
                        StringBuilder s = new StringBuilder();
                        for(int k = 1;k< data.length;k++){
                            s.append(data[k]);
                            if(k < data.length - 1)
                                s.append(",");
                        }
                        System.out.println(s);

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

    /*
    第一步，分割数据源，然后对source1第一行补全,得到sourceDA
     */
    @Test
    public void processOriginSam(){
        // 分割数据源
        String dataPath = "data/new_weather/origin/augdata-weather30,50,35.0.csv";
        File f = new File(dataPath);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()-1);
            for(int i = 1; i<=sourceNum; i++){
                String filePath = "data/new_weather/30,50,35.0/sourceDA/source"+i+".csv";
                File sourceFile = new File(filePath);
                sourceFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(sourceFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.out.println("entity,Domain,Conditions,Humidity,day");
                List<String> entity = new ArrayList<>();
                int outputLines = 0;
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(Integer.parseInt(data[0])==i&&(!(entity.contains(data[1])))){
                        entity.add(data[1]);
                        outputLines++;
                        StringBuilder s = new StringBuilder();
                        for(int k = 1;k< data.length;k++){
                            s.append(data[k]);
                            if(k < data.length - 1)
                            s.append(",");
                        }
                        System.out.println(s);

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
    public void fixSource(){
        int sourceNum=15;
        String path = "data/ctd/weather/sourceUnfix/source2.csv";
        File source13 = new File(path);
        try {
            FileReader fr = new FileReader(source13);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            Map<String,String> entity = new HashMap<>();
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                entity.put(data[1],str);
            }
            for(int i = 1;i<=sourceNum;i++){
                String filePath = "data/ctd/weather/sourceUnfix/source"+i+".csv";
                String outputPath = "data/ctd/weather/source/source"+i+".csv";
                File outputFile = new File(outputPath);
                PrintStream ps = new PrintStream(outputFile);
                System.setOut(ps);

                File fix = new File(filePath);
                FileReader frFix = new FileReader(fix);
                BufferedReader brFix = new BufferedReader(frFix);

                String currentKey;
                String strF;
                String[] dataF;
                while((strF = brFix.readLine())!=null){
                    dataF = strF.split(",",-1);
                    if(entity.containsKey(dataF[1])){
                        currentKey = dataF[1];
                        System.out.println(strF);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    第四步，整合,存在temp中
     */
    @Test
    public void moveTuple(){
        // 标注在前，增强在后
        for(int i = 1;i<=15;i++){
            // 需要放到后面的路径
            File tempFile = new File("data/new_weather/30,50,35.0/weizengqiang/source"+i+".csv");
            try {
                FileReader frTemp = new FileReader(tempFile);
                BufferedReader brTemp = new BufferedReader(frTemp);
                brTemp.readLine();
                String str;
                String[] data;
                // source中在前面的路径，标注路径
                String path = "data/new_weather/30,50,35.0/temp/source"+i+".csv";
                while((str = brTemp.readLine())!=null){
                    appendMethodB(path,str+"\n");
                }
                frTemp.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    public void buDomain(){
         for(int i = 1;i<=15;i++){
             String filePath = "data/new_weather/30,50,35.0/source/source"+i+".csv";
             File f = new File(filePath);
             String fileTemp = "data/new_weather/30,50,35.0/temp/source"+i+".csv";
             try {
                 PrintStream ps = new PrintStream(fileTemp);
                 System.setOut(ps);
                 FileReader fr = new FileReader(f);
                 BufferedReader br = new BufferedReader(fr);
                 String str;
                 String[] data;
                 // 正确的存在temp中
                 while((str = br.readLine())!=null){
                     String newStr = "";
                     String splitStr = ",";
                     data = str.split(",",-1);
                     if(data.length==4){
                         // add
                         newStr += data[0] + splitStr + "Austin" + splitStr + data[1]
                                 + splitStr + data[2] + splitStr + data[3];
                         System.out.println(newStr);
                     }else if(data.length==5){
                         System.out.println(str);
                     }
                 }
                 br.close();
                 fr.close();
                 ps.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }
    }
    @Test
    public void allWeather(){
        int sourceNum = 15;
        int attrKind = 5;
        String filePath = "data/weather/weather_da_sam.csv";
        File f = new File(filePath);
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            String attr = br.readLine();
            br.mark((int)f.length()+10000);
            // set output path

            for(int currentSource = 1;currentSource <= sourceNum;currentSource++){
                String outputFilePath = "data/new_weather/total/sourceDA/source" + currentSource + ".csv";
                File outFile = new File(outputFilePath);
                outFile.createNewFile();
                PrintStream ps = new PrintStream(outFile);
                System.setOut(ps);
                System.out.println("entity,Domain,Conditions,Humidity,day");
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(data[0].equals(String.valueOf(currentSource))){
                        StringBuilder output = new StringBuilder();
                        int i = 1;
                        for(i = 1;i<attrKind;i++){
                            output.append(data[i]).append(",");
                        }
                        output.append(data[i]);
                        System.out.println(output);
                    }
                }
                ps.close();
                br.reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void da(){
        for(int current = 1;current <=15;current++){
            String inputPath = "data/new_weather/total/sourceDA/source" + current + ".csv";
            String outPath = "data/new_weather/total/temp/source" + current + ".csv";
            try {
                FileReader fr = new FileReader(inputPath);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                File f = new File(outPath);
                f.createNewFile();
                PrintStream ps = new PrintStream(f);
                System.setOut(ps);
                int line = 0;
                while((str = br.readLine())!=null&&line<201){
                    data = str.split(",",-1);
                    System.out.println(str);
                    line++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Test
    public void da_tihuan(){
        for(int current = 1;current<=sourceNum;current++){
            String sourcePath = "data/new_weather/total/source/source" + current + ".csv";
            String daPath = "data/new_weather/total/sourceDA/source" + current + ".csv";
            String outputPath = "data/new_weather/total/temp/source" + current + ".csv";
            try {
                PrintStream ps = new PrintStream(outputPath);
                System.setOut(ps);
                FileReader fr = new FileReader(sourcePath);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                String attr = br.readLine();
                System.out.println(attr);
                int line = 0;
                // 前200不要，留给da
                while((str = br.readLine())!= null){
                    data = str.split(",",-1);
                    if(line < 200){
                        line++;
                        continue;
                    }
                    System.out.println(str);
                    line++;
                }
                br.close();
                fr.close();
                // read da 200
                fr = new FileReader(daPath);
                br = new BufferedReader(fr);
                int daLine = 0;
                // 读走属性行
                br.readLine();
                while((str = br.readLine())!=null&&daLine<200){
                    data = str.split(",",-1);
                    System.out.println(str);
                    daLine++;
                }
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Test
    public void quchongTruth(){
        String sourcePath = "data/new_weather/30,50,35.0/threetruth.csv";
        List<String> list = new ArrayList<>();
        File f = new File(sourcePath);
        FileReader fr = null;
        String out = "data/new_weather/30,50,35.0/temp/threetruth.CSV";

        try {
            File outPutFile = new File(out);
            PrintStream ps = new PrintStream(outPutFile);
            System.setOut(ps);
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                if(list.contains(data[0])){
                    continue;
                }
                list.add(data[0]);
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void quchongSource(){

        for(int i = 1;i<=15;i++){
            String sourcePath = "data/new_weather/30,50,35.0/source/source" + i + ".csv";
            List<String> list = new ArrayList<>();
            File f = new File(sourcePath);
            FileReader fr = null;
            String out = "data/new_weather/30,50,35.0/temp/source" + i + ".csv";

            try {
                File outPutFile = new File(out);
                PrintStream ps = new PrintStream(outPutFile);
                System.setOut(ps);
                fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);
                    if(list.contains(data[0])){
                        continue;
                    }
                    list.add(data[0]);
                    System.out.println(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processBestWeather(){
        int truthNum = 187;
        List<Integer> indexList = new ArrayList<>();
        for(int i = 0;i<truthNum;i++){
            int index = (int)(Math.random()*1000);
            if(indexList.contains(index)){
                i--;
            }else {
                indexList.add(index);
            }
        }
        Map<String,String> strMap = new HashMap<>();
        String truthFilePath = "data/new_weather/total/threetruth.CSV";
        File threetruthFile = new File(truthFilePath);
        try {
            FileReader fr = new FileReader(threetruthFile);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                if(indexList.contains(Integer.parseInt(data[0]))){
                    strMap.put(data[0],str);
                }
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //da
        int daNum = 13;
        for(int i = 0;i<daNum;i++){

        }
        // 输入数据
        int sourceNum = 15;
        for(int s = 1;s<=sourceNum;s++){
            String sourceFilePath = "data/new_weather/total/temp/source" + s + ".csv";
            File sourceFile = new File(sourceFilePath);
            try {
                FileReader fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                while((str = br.readLine())!=null){
                    data = str.split(",",-1);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    @Test
    public void easyWeather(){
        int sourceNum = 15;
        String threetruthPath = "data/new_weather/total/threetruth.CSV";
        for(int s = 1;s<=sourceNum;s++){
            String outputPath = "data/new_weather/total/temp/source/source"+s+".csv";
            File outputSourceFile = new File(outputPath);
            try {
                PrintStream ps = new PrintStream(outputSourceFile);
                System.setOut(ps);
                // 187truth
                File threetruth = new File(threetruthPath);
                FileReader fr = null;
                fr = new FileReader(threetruth);
                BufferedReader br = new BufferedReader(fr);
                String str;
                int truthline = 0;
                String attr = br.readLine();
                System.out.println(attr);
                while((str = br.readLine())!=null&&truthline<187){
                    System.out.println(str);
                    truthline++;
                }
                br.close();
                fr.close();
                // 13da
                String daPath = "data/new_weather/total/onlysourceDA/sourceDA/source"+s+".csv";
                File sourceDAfile = new File(daPath);
                fr = new FileReader(sourceDAfile);
                br = new BufferedReader(fr);
                attr = br.readLine();
                int daline = 0;
                int k = 0;
                while(k<187){
                    k++;
                    br.readLine();
                }
                while((str = br.readLine())!=null&&daline<13){
                    System.out.println(str);
                    daline++;
                }
                fr.close();
                br.close();
                //800
                String sourceFilePath = "data/new_weather/total/only-source/source/source"+s+".csv";
                File sourceFile = new File(sourceFilePath);
                fr = new FileReader(sourceFile);
                br = new BufferedReader(fr);
                br.readLine();
                int sourceLine = 0;
                while(sourceLine<200){
                    str = br.readLine();
                    sourceLine++;
                }
                while((str = br.readLine())!=null){
                    System.out.println(str);
                }
                fr.close();
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void easyDAweather(){
        int sourceNum = 15;
        for(int s = 1;s<=sourceNum;s++){
            String sourceDApath = "data/new_weather/total/onlysourceDA/sourceDA/source"+s+".csv";
            File sourceDAfile = new File(sourceDApath);
            String outputPath = "data/new_weather/total/temp/sourceDA/source"+s+".csv";
            File outFile = new File(outputPath);
            try {
                outFile.createNewFile();
                PrintStream ps = new PrintStream(outFile);
                System.setOut(ps);
                FileReader fr = new FileReader(sourceDAfile);
                BufferedReader br = new BufferedReader(fr);
                String attr = br.readLine();
                System.out.println(attr);
                String str;
                int line = 0;
                while(line<187){
                    line++;
                    br.readLine();
                }
                int daLine = 0;
                while((str = br.readLine())!=null&&daLine<13){
                    System.out.println(str);
                    daLine++;
                }
                br.close();
                fr.close();
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void check(){
        int sourceNum = 15;
        int tupleNum = 14;
        for(int s = 1;s<=sourceNum;s++){
            String sourcePath = "data/new_weather/total/temp/sourceDA/source"+s+".csv";
            File sourceFile = new File(sourcePath);
            FileReader fr = null;
            try {
                fr = new FileReader(sourceFile);
                BufferedReader br = new BufferedReader(fr);
                int line = 0;
                while(br.readLine()!=null){
                   line++;
                }

                assert (line==tupleNum);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
