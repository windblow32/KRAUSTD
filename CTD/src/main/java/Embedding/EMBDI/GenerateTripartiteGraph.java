package main.java.Embedding.EMBDI;

import main.java.Embedding.abstruct_Graph.ConcreteEdgesGraph;
import main.java.Embedding.abstruct_Graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateTripartiteGraph{
    private final Graph<String> graph = new ConcreteEdgesGraph<>();
    // RID的set
    private final Set<String> RID_set = new HashSet<>();
    // column的列表
    private final List<String> column_i = new ArrayList<>();
    // 图中所有点nodes
    public List<String> all_nodes = new ArrayList<>();
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

    public GenerateTripartiteGraph generateTripartiteGraph(String file){
        //  修改代码一定注意，对于函数中声明该类对象的函数，当调用成员变量时，必须用该对象引出，否则无法
        //  加入，也就是红色变量必须graph.引出！

        GenerateTripartiteGraph graph = new GenerateTripartiteGraph();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data;
            // 先读取一行，获取attribute的个数
            str=br.readLine();
            data = str.split(",");
            // 存储属性,后面获取Ck会用到
            graph.column_i.addAll(Arrays.asList(data));
            int attribute = 0;
            for (String s : graph.column_i) {
                graph.addVertex(s);
                graph.all_nodes.add(s);
                attribute++;// 记录总属性数
            }
            // 继续读取下面的元组
            int row_id = 0;
            int column = 0;

            //  第二行开始
            double[] current_sum = new double[attribute];// 每次读取一行后，存储每一列的累加和，用于处理null
            while((str=br.readLine())!=null){
                // FIXME:从第0列开始读取
                column = 0;
                List<String> row_i = new ArrayList<>();
                // G.addNode(Ri) Ri is the record id of Ri
                String Ri = "row_" + row_id;
                // 一定是后来指定的graph中的RID_set！！！！
                // 这里debug就离谱
                graph.RID_set.add(Ri);
                graph.addVertex(Ri);
                graph.all_nodes.add(Ri);
                row_id++;
                // String[] data
                data = str.split(",");
                // todo: judge data中的数据是否为null
                //FIXME : 假设修改的数据都是数字类型

                for(int index = 0;index<data.length;index++){
                    Pattern p = Pattern.compile("^\\d+(\\.\\d+)?");
                    Matcher m = p.matcher(data[index]);
                    boolean b = m.matches();
                    if(b){
                        // 如果是数字
                        if(data[index]==null){
                            // 并且为空
                            // FIXME : null 用当前平均值替代
                            data[index] = String.valueOf(current_sum[index]/index);
                        }
                        current_sum[index] += Double.parseDouble(data[index]);
                    }

                }
                // 获取所有Vk存储到row_i中
                row_i.addAll(Arrays.asList(data));
                String[] value;
                List<String> value_i = new ArrayList<>();
                for(String Vk : row_i){
                    if(Vk.contains(" ")){
                        // 说明有multi-word,把每个单词分离出来,然后加入到图中
                       value = Vk.split(" ");
                       // 对于这个属性的成员,每个word储存到value_i中
                       value_i.addAll(Arrays.asList(value));
                       for(String word : value_i){
                           // G.addNode(word)
                           graph.addVertex(word);
                           graph.all_nodes.add(word);
                           // G.addEdge(word,Ri)
                           graph.addEdge(word, Ri);
                           // G.addEdge(word,Ck)
                           graph.addEdge(word, graph.column_i.get(column));
                       }
                    }
                    else{
                        graph.addVertex(Vk);
                        graph.all_nodes.add(Vk);
                        graph.addEdge(Vk, Ri);
                        graph.addEdge(Vk, graph.column_i.get(column));
                    }
                    // 每次处理完一个Vk,就进入下一个属性的处理,column是属性列表column_i的索引.
                    column++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
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
