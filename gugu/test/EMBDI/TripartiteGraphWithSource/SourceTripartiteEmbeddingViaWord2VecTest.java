package EMBDI.TripartiteGraphWithSource;

import EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import com.medallia.word2vec.Searcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SourceTripartiteEmbeddingViaWord2VecTest {

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
            System.out.println(word2VecService.getEmbeddings());
        } catch (Searcher.UnknownWordException e) {
            e.printStackTrace();
        }
    }
}