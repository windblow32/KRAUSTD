package EMBDI.TripartiteWeightedGraphWithSource;

import abstruct_Graph.ConcreteEdgesGraph;
import abstruct_Graph.Graph;
import java.util.Random;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class GenerateSampledTripartite {


    private final Graph<String> graph = new ConcreteEdgesGraph<>();
    // 每个源每行RID的set
    private final Set<String> RID_set = new HashSet<>();
    // 样本sample的set
    private final List<String> Sample_set = new ArrayList<>();
    // 属性名column的列表
    private final List<String> column_i = new ArrayList<>();
    // 图中所有点nodes
    public List<String> all_nodes = new ArrayList<>();

    // 代理Graph中的方法
    public boolean addVertex(String vertex) {
        return graph.add(vertex);
    }

    // 边带有了权值
    public int addEdge(String node1, String node2, int weight) {
        return graph.set(node1, node2, weight);
    }

    public Set<String> vertices() {
        return new HashSet<>(graph.vertices());
    }

    public Map<String, Integer> sources(String target) {
        return new HashMap<>(graph.sources(target));
    }

    public Map<String, Integer> targets(String sources) {
        return new HashMap<>(graph.targets(sources));
    }

    /**
     * 每个数据源的属性都是不同的
     * 每个数据源的sample也是不同的，另外应有整体的sample
     *
     * @param fileList 数据集列表
     * @return
     */
    public GenerateSampledTripartite generateSourceTripartiteGraph(@NotNull List<String> fileList) {
        //  修改代码一定注意，对于函数中声明该类对象的函数，当调用成员变量时，必须用该对象引出，否则无法
        //  加入，也就是红色变量必须graph.引出！

        GenerateSampledTripartite sourceGraph = new GenerateSampledTripartite();
        try {
            // 读取第0个文件，用于初始化attribute等
            String file = fileList.get(0);
            FileReader fd = new FileReader(file);
            BufferedReader br = new BufferedReader(fd);
            // 文件操作所需变量
            String str = null;
            // 存储str读取的一行的值
            String[] data = null;
            // 首先读取一行，作为属性
            str = br.readLine();
            data = str.split(",");
            // 存储全部属性
            sourceGraph.column_i.addAll(Arrays.asList(data));
            for (String s : sourceGraph.column_i) {
                // store attribute into sourceGraph
                sourceGraph.addVertex(s);
                sourceGraph.all_nodes.add(s);
            }
            // 所有文件中sample点是公共的格式，应该只加入一次
            // 用sample_i表示样本条目i
            // FIXME :处理完第一个文件，就加入了一些sample，此后不在加入即可

            // 按顺序读取文件
            int column = 0;
            // FIXME:读取当前文件,是第i个文件，则有：
            int fileNum = 0;
            // 判断是否是最后一个文件(真值)
            int truthFlag = 0;
            // 创建两个点，数据源总点SOURSE，和样本总点SAMPLE
            String SOURCE = "SOURSE";
            String SAMPLE = "SAMPLE";
            sourceGraph.addVertex(SOURCE);
            sourceGraph.addVertex(SAMPLE);
            sourceGraph.all_nodes.add(SOURCE);
            sourceGraph.all_nodes.add(SAMPLE);
            for (String files : fileList) {
                FileReader fr = new FileReader(files);
                BufferedReader newBr = new BufferedReader(fr);
                // 每个文件是不同的数据源
                String source_id = "source_" + fileNum;
                sourceGraph.addVertex(source_id);
                sourceGraph.all_nodes.add(source_id);
                // source与总点SOURSE连接
                sourceGraph.addEdge(source_id, SOURCE, 1);

                // 先读出来一行，不用头部
                newBr.readLine();
                // 从第二行开始处理
                // FIXME : process null using avg(now use 0)
                double[] current_sum = new double[sourceGraph.column_i.size()];
                int row_id = 0;
                // 判断是否是最后一个文件，即真值文件，如果是，就进行标记
//                if(fileList.get(fileList.size()-1).equals(files)){
//                    truthFlag = 1;
//                }
                // 正则匹配，如果名字中带有truth，就认为是真值文件，取消了对于文件输入顺序的要求
                if (judgeTruthFile(files)) {
                    truthFlag = 1;
                }
                while ((str = newBr.readLine()) != null) {
                    // 每一行是不同的tuple和row_id
                    // String tuple = "tuple_" + row_id;
                    // 为每个数据源的表分配row的id
                    column = 0;
                    List<String> row_i = new ArrayList<>();
                    // add node row_id
                    // row_1代表第一个元组
                    // row_1_s1来自第1个数据源的第一行，来自真值
                    // String Ri = "row_" + row_id + "_s" + fileNum;
                    String Ri;
                    // 区分了不同数据源的row，对于普通数据源加后缀yi
                    if (truthFlag == 0) {
                        Ri = "row_" + row_id + "_y" + fileNum;
                    }
                    // 真值数据集中的元组加s后缀
                    else {
                        Ri = "row_" + row_id + "_s" + fileNum;
                    }
//                    if(!sourceGraph.RID_set.contains(Ri)){
//                        sourceGraph.RID_set.add(Ri);
//                    }
                    // set中的元素就是互斥的
                    sourceGraph.RID_set.add(Ri);

                    // 添加样本点，个数等于每个数据集中的元组数，只添加一次
                    if (fileNum == 0) {
                        String sample = "sample_" + row_id;
                        sourceGraph.addVertex(sample);
                        sourceGraph.all_nodes.add(sample);
                        sourceGraph.Sample_set.add(sample);
                        // 每个sample连接总点SAMPLE
                        sourceGraph.addEdge(sample, SAMPLE, 1);
                    }
                    // Ri添加进入图中
                    sourceGraph.addVertex(Ri);
                    if (!sourceGraph.all_nodes.contains(Ri)) {
                        sourceGraph.all_nodes.add(Ri);
                    }
                    // 将Ri,即每个源的每个row与这个数据源连接
                    sourceGraph.addEdge(Ri, source_id, 1);
                    // 并且每个row和对应行的sample连接,权重为0
                    sourceGraph.addEdge(Ri, sourceGraph.Sample_set.get(row_id), 1);
                    // 处理完了一行
                    row_id++;
                    data = str.split(",");
                    // FIXME:假设表中的数值都是数字类型，现在判断是否为空
                    for (int index = 0; index < data.length; index++) {
                        if (data[index] == null || data[index].equals("")) {
                            data[index] = String.valueOf(0);
                            // true
                            // 并且为空
//                            // FIXME : null 用当前平均值替代
//                            data[index] = String.valueOf(current_sum[index]/index);
////                        if(judge(data[index])){
////                            data[index] = String.valueOf(current_sum[index]/index);
////                        }
//                            current_sum[index] += Double.parseDouble(data[index]);
                        }
                    }
                    // row_i暂存每行的数据
                    row_i.addAll(Arrays.asList(data));
//                    String[] value = null;
//                    List<String> value_i = new ArrayList<>();

//                    for(String Vk:row_i ){
//                        if(Vk.contains(" ")) {
//                            value = Vk.split(" ");
//                            // 对于这个属性的成员,每个word储存到value_i中
//                            value_i.addAll(Arrays.asList(value));
//                            for (String word : value_i) {
//                                // G.addNode(word)
//                                sourceGraph.addVertex(word);
//                                sourceGraph.all_nodes.add(word);
//                                // G.addEdge(word,Ri)
//                                sourceGraph.addEdge(word, Ri);
//                                // G.addEdge(word,Ck)
//                                sourceGraph.addEdge(word, sourceGraph.column_i.get(column));
//                                sourceGraph.addEdge(word, source_id);
//                            }
//                        }
//                        else{
//                            sourceGraph.addVertex(Vk);
//                            sourceGraph.all_nodes.add(Vk);
//                            // 将数值和属性连接
//                            sourceGraph.addEdge(Vk,sourceGraph.column_i.get(column));
//                            sourceGraph.addEdge(Vk,source_id);
//                            sourceGraph.addEdge(Vk,Ri);
//                        }
//                        column++;
//                    }
                    // 平面图方向，连接了source和row_id
                    // sourceGraph.addEdge(source_id,Ri);
                    // sourceGraph.addEdge(tuple,Ri);
                    // 每个Ri和所有属性相连，权值是属性值

                    for (int t = 0; t < data.length; t++) {
                        int temp = Math.abs((int) Double.parseDouble(data[t]));
                        if (temp == 0) {
                            // 原有属性是1的，变成2
                            temp = 1; // 加完1是2
                        }
                        // 讨论：（1）普通的边，没有属性值，现在权重是1
                        // 原有的权重是0的，现在是2
                        // 原有是1的，现在是2.余下都是原有加1
                        // 现有是1的就是原来不存在的边
                        sourceGraph.addEdge(Ri, sourceGraph.column_i.get(t), temp + 1);
                    }
                }
                fileNum++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceGraph;
    }

    public Set<String> getRID_set() {
        return new HashSet<>(this.RID_set);
    }

    public List<String> getColumn_i() {
        return new ArrayList<>(this.column_i);
    }

    /**
     * @return token后的nodes
     */
    public List<String> getAll_nodes() {
        return new ArrayList<>(this.all_nodes);
    }

    /**
     * @param fileName 文件名字
     * @return 如果文件中带有truth字段，返回true，代表是真值文件
     */
    private boolean judgeTruthFile(String fileName) {
        String regex = ".truth.";
        return Pattern.matches(regex, fileName);
    }

    private double N(int a, int b){
        Random r = new Random();
        return Math.sqrt(b)* r.nextGaussian()+a;
    }


}

