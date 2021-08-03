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

        List<Double> sourceDistanceList = new ArrayList<>();
        int trainSourceNum = fileList.size()-1;
        // FIXME:指定真值文件数据源序号
        String truth = "source_3";
        for(int i = 0;i<trainSourceNum;i++){
            String source = "source_" + i;
            double d = word2VecService.distance(source,truth);
            sourceDistanceList.add(d);
            System.out.println("source_" + i + "与真值余弦相似度 : " + d);
        }


        System.out.println("*****************************************");
        double sum = 0;
        for(int i = 0;i<trainSourceNum;i++){
            sum += sourceDistanceList.get(i);
        }

        for(int i = 0;i<trainSourceNum;i++){
            System.out.println("source_" + i + "与真值相似度占比 : " + sourceDistanceList.get(i)/sum);
        }

        // source之间相似度
        System.out.println("*****************************************");
        for(int i = 0;i<trainSourceNum;i++){
            for(int j = i+1;j<trainSourceNum;j++){
                String s1 = "source_" + i;
                String s2 = "source_" + j;
                double d_ij = word2VecService.distance(s1,s2);
                System.out.println(s1 + "与" + s2 + " embedding 相似度 : " + d_ij);
            }
        }

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
        for(int i = 0;i<fileList.size()-1;i++){
            String source = "source_" + i;
            double domainDistance = word2VecService.distance("volumn",source);
            System.out.println(source + "与volumn列的相似度为 : " + domainDistance );
        }

    }
}