package main.java.metaLearning;

import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import main.java.Embedding.EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import org.junit.Test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AttributeScore {
    PrintStream out = System.out;
    @Test
    public void calculateEmbedding(){
        String filePath = "E:\\GitHub\\EntityMatching\\data\\abt-buy\\labeled abt-buy.csv";
        List<String> fileList = new ArrayList<>();
        fileList.add(filePath);
        SourceTripartiteEmbeddingViaWord2Vec word2Vec = new SourceTripartiteEmbeddingViaWord2Vec();
        word2Vec.train(fileList,3,3,10,3000);
        Word2VecModel model;
        model = word2Vec.trainWithLocalWalks();
        String v1 = "ltable_id";
        String v2 = "rtable_id";
        double res = 1 - word2Vec.distanceUseSavedModel(model, v1, v2);
        System.setOut(out);
        System.out.println(res);
    }
    @Test
    public void run(){
        String filePath = "E:\\GitHub\\EntityMatching\\data\\abt-buy\\labeled abt-buy.csv";
        List<String> fileList = new ArrayList<>();
        fileList.add(filePath);
        NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2Vec = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
        String graphFilePath = null;
        word2Vec.train(fileList, null, 3, 3, 60, 20000,
                5, 5, 5, 5,
                5, 5, 1, 1, 1, 160, 6);
        Word2VecModel model;
        model = word2Vec.trainWithLocalWalks(null);
        String v1 = "ltable_id";
        String v2 = "rtable_id";
        double res = 1 - word2Vec.distanceUseSavedModel(model, v1, v2);
        System.setOut(out);
        System.out.println(res);
    }


}
