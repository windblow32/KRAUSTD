package EMBDI.Experiment;

import EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SaveHeap {
    /**
     * first run ExtractSampleBasedStock.java, save walkPath
     * then use this method to start train
     * todo: load model
     */
    @Test
    public void test() throws Searcher.UnknownWordException, IOException {
        SourceTripartiteEmbeddingViaWord2Vec word2VecService = new SourceTripartiteEmbeddingViaWord2Vec();
        // String walkPath = "data/stock100/walkList5.txt";
        String modelPath = "model/Tri/stock100/total9.model";
        // K_map store k-value pair, use saved model get embeddings
        Map<String,List<Double>> K_map = word2VecService.getRandom_Source_Embeddings(modelPath);
        File f=new File("log/Stock/stock100/55sourceEMBDI_useModel9.txt");
        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
        Set<Map.Entry<String,List<Double>>> entrySet = K_map.entrySet();
        Iterator<Map.Entry<String, List<Double>>> it2 = entrySet.iterator();
        while(it2.hasNext()){
            Map.Entry<String,List<Double>> entry = it2.next();
            String ID = entry.getKey();
            List<Double> stu = entry.getValue();
            System.out.println(ID+" "+stu);
        }
        fileOutputStream.close();

    }
}
