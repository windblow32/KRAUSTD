package EMBDI;


import com.google.common.collect.Lists;
import com.medallia.word2vec.*;
import com.medallia.word2vec.neuralnetwork.NeuralNetworkType;
import com.medallia.word2vec.util.Format;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meta_Algorithm_for_EMBDI {
    public List<String> nodes = new ArrayList<>();

    /**
     * @param file    文件路径
     * @param n_walks number of random walks
     * @param n_nodes number of nodes
     * @param length  embedding的长度
     */
    // todo: 返回值不应该是void，查看Generate中的返回值修改
    public List<String> Meta_Algorithm(String file, int n_walks, int n_nodes, int length) {
        List<String> walks = new ArrayList<>();
        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        graph = graph.generateTripartiteGraph(file);
        GenerateRandomWalk walkgraph = new GenerateRandomWalk(graph);
        nodes = graph.getAll_nodes();
        for (String str : nodes) {
            for (int i = 0; i < n_walks / n_nodes; i++) {
                List<String> list = walkgraph.randomWalk(str, length);
//                StringBuilder sentence = null;
//                for (int index = 0; index < length; index++) {
//                    sentence = (sentence == null ? new StringBuilder("null") : sentence).append(list.get(index));
//                }
//
//                walks.add(sentence == null ? null : sentence.toString());
                walks.addAll(list);
            }
        }
//        Word2Vec method = new Word2Vec();
//         method.generateEmbedding(walks);
        return walks;
    }


}
