package main.java;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static main.java.Embedding.fastText.LoadModel.readData;
import static main.java.Embedding.fastText.LoadModel.readEmbeddings;

public class test {
    @Test
    public void getDataEmbeddings() throws IOException {
        String embeddingFile = "E:\\GitHub\\pythonProject\\saved_model\\glove.42B.300d.txt";
        File emb = new File(embeddingFile);
        BufferedReader reader = new BufferedReader(new FileReader(emb));
        String line;
        File output = new File("model/Tri/CTD/monitor/1.txt");
        PrintStream ps = new PrintStream(output);
        System.setOut(ps);
        List<String> wordList = new ArrayList<>();
        for(int source = 1;source<=5;source++){
            wordList.addAll(readData("E:\\GitHub\\KRAUSTD\\CTD\\data\\monitor0707\\source\\source"+source +".csv"));
        }
        List<String> outWord = new ArrayList<>();
        for(String w : wordList){
            if(!outWord.contains(w)){
                outWord.add(w);
                reader = new BufferedReader(new FileReader(emb));
                long startTime = System.currentTimeMillis();
                while ((line = reader.readLine()) != null) {
                    long currentTime = System.currentTimeMillis();

                    // 检查循环时间是否超过1秒
                    if (currentTime - startTime > 10000) {
                        // System.out.println("Loop time exceeded 5 seconds. Exiting loop.");
                        break;
                    }
                    String[] parts = line.split("\\s+");
                    String word = parts[0];
                    if(w.toLowerCase().equals(word)){
                        System.out.println(line);
                        break;
                    }
                }
                reader.close();
            }
        }
//        Map<String, float[]> wordEmbeddings = readEmbeddings(embeddingFile);
//        for(String w : wordList){
//            wordEmbeddings.get(w);
//        }
    }
    @Test
    public void process(){
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("python E:\\GitHub\\KRAUSTD\\CTD\\bayesian_optimization.py");// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
