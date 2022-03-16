package main.java.Embedding.EMBDI.SourceEmbedding;

import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.GenerateNormalizeDistributeSourceTripartite;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.GenerateNormalizeDistributeSourceTripartiteRandomWalk;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MetaAlgorithm {
    public List<String> nodes = new ArrayList<>();

    /**
     * @param fileList    文件路径
     * @param n_walks number of random walks
     * @param n_nodes number of nodes
     * @param length  embedding的长度
     */
    // todo: 返回值不应该是void，查看Generate中的返回值修改
    public List<String> Meta_Algorithm(List<String> fileList, int n_walks, int n_nodes, int length) {
        List<String> walks = new ArrayList<>();
        GenerateSourceGraph graph = new GenerateSourceGraph();
        graph = graph.GenerateSourceTripartiteGraph(fileList);
        GenerateSourceRandomWalk walkGraph = new GenerateSourceRandomWalk(graph);
        nodes = graph.getAll_nodes();
        for (String str : nodes) {
            for (int i = 0; i < n_walks / n_nodes; i++) {
                List<String> list = walkGraph.randomWalk(str, length);
                walks.addAll(list);
            }
        }

        return walks;
    }

    public List<String> Meta_AlgorithmUseGraphFilePath(String graphFilePath, int n_walks, int n_nodes, int length) {
        List<String> walks = new ArrayList<>();
        FileInputStream fileInputStream = null;
        GenerateSourceGraph graph = new GenerateSourceGraph();
        try {
            fileInputStream = new FileInputStream(graphFilePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            graph = (GenerateSourceGraph) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("graphFile can't be found!");
            e.printStackTrace();
        }
        GenerateSourceRandomWalk walkGraph = new GenerateSourceRandomWalk(graph);
        nodes = graph.getAll_nodes();
        for (String str : nodes) {
            for (int i = 0; i < n_walks / n_nodes; i++) {
                List<String> list = walkGraph.randomWalk(str, length);
                walks.addAll(list);
            }
        }

        return walks;
    }
}
