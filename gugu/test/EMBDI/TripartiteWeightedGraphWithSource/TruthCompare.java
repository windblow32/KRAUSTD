package EMBDI.TripartiteWeightedGraphWithSource;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TruthCompare {
    public int attribute = 10;

    @Test
    public void compareFiles(){
        // 存储最终的vector
        String[][] vector = new String[10][10];
        for(int t = 0;t<10;t++){
            for(int q = 0;q<10;q++){
                vector[t][q] = "";
            }
        }
        String truthFilePath = "data/generateSample/truth/Test2Truth.csv";
        List<String> fileList = new ArrayList<>();
        int sourceNum = 55;
        for(int i = 0;i<sourceNum;i++){
            int temp = i + 1;
            String filePath = "data/generateSample/dividedSource/test2/source" + temp + ".csv";
            fileList.add(filePath);
        }
        // 维护数组，先读取真值，存储所有的真值，然后比较，如果相等，就把矩阵的对应位置加一个源的名字
        try {
            FileReader fr = new FileReader(truthFilePath);
            BufferedReader br = new BufferedReader(fr);
            // 读取一行，不要属性的适配
            br.readLine();
            String str = null;
            String[] data = null;
            // 10 samples, 10 attributes
            String[][] value = new String[10][10];
            int row = 0;

            while((str = br.readLine())!=null){
                data = str.split(",");
                if (attribute >= 0) System.arraycopy(data, 0, value[row], 0, attribute);
                row++;
            }
            br.close();
            fr.close();
            int fileID = 1;
            for(String sourceFile : fileList){
                FileReader sourceFr = new FileReader(sourceFile);
                BufferedReader sourceBr = new BufferedReader(sourceFr);
                // 除去属性行
                sourceBr.readLine();
                String sourceStr = null;
                String[] sourceData = null;
                // 10 samples, 10 attributes
                String[][] sourceValue = new String[10][10];
                int newRow = 0;
                while((sourceStr = sourceBr.readLine())!=null){
                    sourceData = sourceStr.split(",",-1);
                    for(int i = 0;i<attribute;i++){
                        // equal to truth value
                        if(sourceData[i].equals(value[newRow][i])){
                            String temp = vector[newRow][i];
                            // add source seq
                            vector[newRow][i] = temp.concat(" " + fileID);
                        }

                    }
                    newRow++;
                }

                sourceBr.close();
                sourceFr.close();
                fileID++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultPath = "data/generateSample/truthCompare/Test3truthCompareResultWithJaccard.txt";
        File f = new File(resultPath);
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
            for(int i = 0;i<10;i++){
                System.out.println("***************************");
                System.out.println("sample " + i);
                for(int j = 0;j<10;j++){
                    int temp = j + 1;
                    System.out.println("attribute " + temp);
                    System.out.println(vector[i][j]);
                }
            }
            // calc Jaccard
            for(int i = 0;i<10;i++){
                for(int j = i+1;j<10;j++){
                    // calc Jaccard for sample i and j, attribute = 8 or 9
                    String[] data_i = vector[i][8].split(" ",-1);
                    String[] data_j = vector[j][8].split(" ",-1);
                    List<String> list1 = Arrays.asList(data_i);
                    List<String> list2 = Arrays.asList(data_j);
                    // 三种：
                    // 1:i有但是j没有，i没有但是j有，i和j都有
                    int f10 = 0;
                    int f01 = 0;
                    int f11 = 0;
                    for(int t = 1;t<55;t++){
                        String str = String.valueOf(t);
                        if (list1.contains(str)&&(!list2.contains(str))){
                            f10++;
                        }
                        if (list1.contains(str)&&(list2.contains(str))){
                            f11++;
                        }
                        if (!list1.contains(str)&&(list2.contains(str))){
                            f01++;
                        }
                    }
                    double result = f11/(double)(f10+f01+f11);
                    System.out.println("sample " + i +" and sample " + j + " Jaccard similarity : " + result);
                }
            }
            // System.out.println(Arrays.deepToString(vector));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
