package EMBDI;

import org.junit.Test;

public class GenerateRandomWalkTest {

    @Test
    public void findNeiboringRID() {
        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        graph = graph.generateTripartiteGraph("./data/test.csv");
        GenerateRandomWalk walkgraph = new GenerateRandomWalk(graph);
        walkgraph.findNeiboringRID("1");
        System.out.println(walkgraph.findNeiboringRID("1"));
    }

    @Test
    public void findRandomNeighbor() {
        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        graph = graph.generateTripartiteGraph("./data/test.csv");
        GenerateRandomWalk walkgraph = new GenerateRandomWalk(graph);
        walkgraph.findRandomNeighbor("1");
        System.out.println(walkgraph.findRandomNeighbor("1"));
    }

    @Test
    public void randomWalk() {
        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        graph = graph.generateTripartiteGraph("./data/test.csv");
        GenerateRandomWalk walkgraph = new GenerateRandomWalk(graph);
        System.out.println(walkgraph.randomWalk("1", 50));
    }
}