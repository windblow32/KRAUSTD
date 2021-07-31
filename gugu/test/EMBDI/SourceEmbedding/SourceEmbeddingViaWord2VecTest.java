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
        List<Double> vector = word2VecService.train(fileList,20,3,3);
        Map<String, List<Double>> EM = new HashMap<>();
        try {
            EM = word2VecService.getEmbeddings();
            // System.out.println(word2VecService.getEmbeddings());
            File f=new File("log/SourceTripartiteGraphEMBDI.txt");
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


//        // 待比较的词
//        String str1 = "e";
//        String str2 = "2";
//
//        System.out.println(word2VecService.distance(str1,str2));
//        System.out.println(vector.size());
    }
}