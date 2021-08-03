package main.java.Embedding.EMBDI.SourceEmbedding;

import main.java.Embedding.abstruct_Graph.ConcreteEdgesGraph;
import main.java.Embedding.abstruct_Graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用于建立数据源的embedding的三分图，分为平面上的<tuple, a(i), Si>,以及竖平面上的<a(i), a(ij), Ai>
 */
public class GenerateSourceGraph {
    private final Graph<String> graph = new ConcreteEdgesGraph<>();
    // RID的set
    private final Set<String> RID_set = new HashSet<>();
    // column的列表
    private final List<String> column_i = new ArrayList<>();
    // 记录右侧总tuple数量
    private List<String> tupleList = new ArrayList<>();
    // 图中所有点nodes
    public List<String>  all_nodes = new ArrayList<>();
    // 代理Graph中的方法
    public boolean addVertex(String vertex){
        return graph.add(vertex);
    }
    public int addEdge(String node1, String node2){
        return graph.set(node1, node2, 1);
    }
    public Set<String> vertices(){
        return new HashSet<>(graph.vertices());
    }
    public Map<String,Integer> sources(String target){
        return new HashMap<>(graph.sources(target));
    }
    public Map<String, Integer> targets(String sources){
        return new HashMap<>(graph.targets(sources));
    }

    public GenerateSourceGraph GenerateSourceTripartiteGraph(List<String> fileList){
        GenerateSourceGraph sourceGraph = new GenerateSourceGraph();
        try {
            // 读取第0个文件，用于初始化attribute等
            String file = fileList.get(0);
            FileReader fd = new FileReader(file);
            BufferedReader br = new BufferedReader(fd);
            String str;
            // 存储str读取的一行的值
            String[] data;
            // 首先读取一行，作为属性
            str = br.readLine();
            data = str.split(",");
            // 存储全部属性
            sourceGraph.column_i.addAll(Arrays.asList(data));
            for(String s : sourceGraph.column_i){
                // store attribute into sourceGraph
                sourceGraph.addVertex(s);
                sourceGraph.all_nodes.add(s);
            }
            // 按顺序读取文件
            int column = 0;
            // FIXME:读取当前文件,是第i个文件，则有：
            int fileNum = 0;
            for(String files: fileList){
                FileReader fr = new FileReader(files);
                BufferedReader newBr = new BufferedReader(fr);
                // 每个文件是不同的数据源
                String source_id = "source_" + fileNum;
                sourceGraph.addVertex(source_id);
                sourceGraph.all_nodes.add(source_id);
                // 从第二行开始处理
                // process null using avg
                double[] current_sum = new double[column_i.size()];
                int row_id = 0;
                while((str=newBr.readLine())!=null){
                    // 每一行是不同的tuple和row_id
                    String tuple = "tuple_" + row_id;
                    sourceGraph.addVertex(tuple);
                    sourceGraph.all_nodes.add(tuple);
                    // 为每个数据源的表分配row的id
                    column = 0;
                    List<String> row_i = new ArrayList<>();
                    // add node row_id
                    // row_1_s1来自第1个数据源的第一行
                    String Ri = "row_" + row_id + "_s" + fileNum;
                    sourceGraph.RID_set.add(Ri);
                    sourceGraph.addVertex(Ri);
                    sourceGraph.all_nodes.add(Ri);
                    row_id++;
                    data = str.split(",");
                    // FIXME:假设表中的数值都是数字类型，现在判断是否为空
                    for(int index = 0;index<data.length;index++){
                        if(data[index]==null){
                            // true
                            // 并且为空
                            // FIXME : null 用当前平均值替代
                            data[index] = String.valueOf(current_sum[index]/index);
//                        if(judge(data[index])){
//                            data[index] = String.valueOf(current_sum[index]/index);
//                        }
                            current_sum[index] += Double.parseDouble(data[index]);
                        }
                    }
                    // row_i暂存每行的数据
                    row_i.addAll(Arrays.asList(data));
                    String[] value;
                    List<String> value_i = new ArrayList<>();

                    for(String Vk:row_i ){
                        if(Vk.contains(" ")) {
                            value = Vk.split(" ");
                            // 对于这个属性的成员,每个word储存到value_i中
                            value_i.addAll(Arrays.asList(value));
                            for (String word : value_i) {
                                // G.addNode(word)
                                sourceGraph.addVertex(word);
                                sourceGraph.all_nodes.add(word);
                                // G.addEdge(word,Ri)
                                sourceGraph.addEdge(word, Ri);
                                // G.addEdge(word,Ck)
                                sourceGraph.addEdge(word, sourceGraph.column_i.get(column));

                            }
                        }
                        else{
                            sourceGraph.addVertex(Vk);
                            sourceGraph.all_nodes.add(Vk);
                            // 将数值和属性连接
                            sourceGraph.addEdge(Vk,sourceGraph.column_i.get(column));
                        }
                        column++;
                    }
                    // 平面图方向，连接了source和row_id
                    sourceGraph.addEdge(source_id,Ri);
                    sourceGraph.addEdge(tuple,Ri);
                }
                fileNum++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceGraph;
    }

    private boolean judge(String str){
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
    }
    public Set<String> getRID_set(){
        return new HashSet<>(this.RID_set);
    }
    public List<String> getColumn_i(){
        return new ArrayList<>(this.column_i);
    }

    /**
     *
     * @return token后的nodes
     *
     *
     */
    public List<String> getAll_nodes(){return new ArrayList<>(this.all_nodes);}
}
