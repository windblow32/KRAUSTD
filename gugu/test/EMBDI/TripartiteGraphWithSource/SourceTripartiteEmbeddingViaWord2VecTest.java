package EMBDI.TripartiteGraphWithSource;

import EMBDI.Conflict;
import EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;

public class SourceTripartiteEmbeddingViaWord2VecTest {

    @Test
    public void train() {
        SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
        List<String> fileList = new ArrayList<>();
        fileList.add("data/sourceDataCSV/sourceData1.CSV");
        fileList.add("data/sourceDataCSV/sourceData2.CSV");
        fileList.add("data/sourceDataCSV/sourceData3.CSV");
        fileList.add("data/sourceDataCSV/truth.CSV");
        List<Double> vector = word2VecService.train(fileList,20,3,3);
        Map<String, List<Double>> EM = new HashMap<>();
        try {
            EM = word2VecService.getEmbeddings();
            // System.out.println(word2VecService.getEmbeddings());
            File f=new File("log/SourceThreeEMBDI.txt");
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
//            System.out.println(word2VecService.getEmbeddings());
            Set<Map.Entry<String,List<Double>>> entrySet = EM.entrySet();
            Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
            while(it2.hasNext()){
                Map.Entry<String,List<Double>> entry = it2.next();
                String ID = entry.getKey();
                List<Double> stu = entry.getValue();
                System.out.println(ID+" "+stu);
            }
        } catch (Searcher.UnknownWordException | IOException e) {
            e.printStackTrace();
        }

        // 待比较的词
        //source0
        String str0 = "source_0";
        String truth = "source_3";
        double d0 = word2VecService.distance(str0,truth);
        System.out.println("source_0与真值余弦相似度 : " + d0);
        // source1
        String str1 = "source_1";
        double d1 = word2VecService.distance(str1,truth);
        System.out.println("source_1与真值余弦相似度 : " + d1);
        // source2
        String str2 = "source_2";
        double d2 = word2VecService.distance(str2,truth);
        System.out.println("source_2与真值余弦相似度 : " + d2);

        System.out.println("*****************************************");
        System.out.println("source_0与真值相似度占比 : " + d0/(d0+d1+d2));
        System.out.println("source_1与真值相似度占比 : " + d1/(d0+d1+d2));
        System.out.println("source_2与真值相似度占比 : " + d2/(d0+d1+d2));

        // source之间相似度
        System.out.println("*****************************************");
        double d01 = word2VecService.distance(str0,str1);
        System.out.println("source_0与source_1 embedding 相似度 : " + d01);
        double d02 = word2VecService.distance(str0,str2);
        System.out.println("source_0与source_2 embedding 相似度 : " + d02);
        double d12 = word2VecService.distance(str1,str2);
        System.out.println("source_1与source_2 embedding 相似度 : " + d12);

        // 样本相似度，实际是用真值的一行，对应于五分图中tuple
        System.out.println("*****************************************");
        // 比较topK，此处k = 4
        for(int k = 0;k<4;k++){
            // 表示图中第k行字符串
            String s1 = "row_" + k;
            String s2 = "row_" + k + "_s3";
            double d_TupleKAndTruthK = word2VecService.distance(s1,s2);
            System.out.println("row" + k + "样本相似度为 : " + d_TupleKAndTruthK);
        }
        System.out.println("*****************************************");
        System.out.println("vector总大小 : " + vector.size());
        Conflict conflict = new Conflict();
        List<Double> conflictList =  conflict.calcConflict(fileList);
        for(int i = 0;i<fileList.size()-1;i++){
            System.out.println("source_"+ i +"的平均冲突为 : " + conflictList.get(i));
        }

        // domain feature
        List<String> attribute = new ArrayList<>();
    }
}