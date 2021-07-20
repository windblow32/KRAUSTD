package EMBDI;


import com.medallia.word2vec.Searcher;
import org.junit.Test;

public class GenerateTripartiteGraphTest {

    @Test
    public void testgenerateTripartiteGraph() throws Searcher.UnknownWordException {
        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        graph = graph.generateTripartiteGraph("./data/test.csv");
//        System.out.println(graph.getRID_set());
//        System.out.println(graph.getColumn_i());
//        System.out.println(graph.getAll_nodes());
    }
}