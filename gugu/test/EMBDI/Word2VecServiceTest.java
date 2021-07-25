package EMBDI;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class Word2VecServiceTest {

    @Test
    public void train() throws Searcher.UnknownWordException {
        Word2VecService word2VecService = new Word2VecService();
        List<Double> vector = word2VecService.train("./data/test.csv",20,3,3);
        word2VecService.getEmbeddings();
        System.out.println(word2VecService.getEmbeddings());
//        System.out.println(vector.size());

    }
}