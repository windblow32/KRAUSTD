package main.java.Embedding.fastText;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadModel {

    public static void main(String[] args) throws IOException {
        // 读取嵌入向量文件
        String embeddingFile = "E:\\GitHub\\pythonProject\\glove\\embedding_vectors.txt";
        Map<String, float[]> wordEmbeddings = readEmbeddings(embeddingFile);

        // 查询单词的嵌入向量
        String word = "asus";
        float[] embedding = wordEmbeddings.get(word);
        String word2 = "tree apple";
        float[] embedding2 = wordEmbeddings.get(word);
        String word3 = "pearl";
        float[] embedding3 = wordEmbeddings.get(word);

        if (embedding != null) {
            // 打印嵌入向量的维度和值
            System.out.println("Dimension: " + embedding.length);
            System.out.println("Dimension: " + embedding2.length);
            System.out.println("Dimension: " + embedding3.length);
            System.out.print("Embedding: ");
            for (double value : embedding) {
                System.out.print(value + " ");
            }
            System.out.println();
        } else {
            System.out.println("Word not found in embeddings.");
        }
    }

    public static void getDataEmbeddings(String embeddingFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(embeddingFile));
        String line;
        File output = new File("");
        PrintStream ps = new PrintStream(output);
        System.setOut(ps);
        List<String> wordList = new ArrayList<>();
        for(int source = 1;source<=5;source++){
            wordList.addAll(readData("E:\\GitHub\\KRAUSTD\\CTD\\data\\monitor0707\\source\\source"+source +".csv"));
        }
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            String word = parts[0];
            if(wordList.contains(word)){
                System.out.println(line);
            }
        }
        reader.close();
    }

    public static Map<String, float[]> readEmbeddings(String embeddingFile) throws IOException {
        Map<String, float[]> wordEmbeddings = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(embeddingFile));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            String word = parts[0];
            float[] embedding = new float[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                embedding[i - 1] = Float.parseFloat(parts[i]);
            }
            wordEmbeddings.put(word, embedding);
        }
        reader.close();
        return wordEmbeddings;
    }

    public static List<String> readData(String path){
        File dataset = new File(path);
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataset));
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",");
                for(String s : data){
                    if(!list.contains(s)){
                        list.add(s);
                    }
                    if(s.contains(" ")){
                        String[] detail = s.split(" ");
                        for(String d : detail){
                            if(!list.contains(d)){
                                list.add(d);
                            }
                        }
                    }
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

