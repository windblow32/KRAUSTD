package EMBDI.SourceEmbedding;

import EMBDI.Word2VecService;
import com.medallia.word2vec.Searcher;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;

public class SourceEmbeddingViaWord2VecTest {

    @Test
    public void train() {
        SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
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
            File f=new File("log/SourceFiveEMBDI.txt");
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
        String str0 = "source_0";
        String truth = "source_3";
        double d0 = word2VecService.distance(str0,truth);
        System.out.println("source_0与真值距离 : " + d0);


        String str1 = "source_1";
        double d1 = word2VecService.distance(str1,truth);
        System.out.println("source_1与真值距离 : " + d1);

        String str2 = "source_2";
        double d2 = word2VecService.distance(str2,truth);
        System.out.println("source_2与真值距离 : " + d2);
        System.out.println("*****************************************");
        System.out.println("source_0与真值距离distance占比 : " + d0/(d0+d1+d2));
        System.out.println("source_1与真值距离distance占比 : " + d1/(d0+d1+d2));
        System.out.println("source_2与真值距离distance占比 : " + d2/(d0+d1+d2));
        System.out.println(vector.size());

    }

}