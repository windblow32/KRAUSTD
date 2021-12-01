package EMBDI;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import org.junit.Test;


import java.util.*;

import static org.junit.Assert.*;

public class Word2VecServiceTest {

    @Test
    public void train() throws Searcher.UnknownWordException {
        Word2VecService word2VecService = new Word2VecService();
        List<Double> vector = word2VecService.train("./data/test.csv",20,3,3);
        Map<String, List<Double>> EM = new HashMap<>();
        EM = word2VecService.getEmbeddings();
        System.out.println(word2VecService.getEmbeddings());

        // 待比较的词
        String str1 = "e";
        String str2 = "2";

        System.out.println(word2VecService.distance(str1,str2));
//        System.out.println(vector.size());

    }
}