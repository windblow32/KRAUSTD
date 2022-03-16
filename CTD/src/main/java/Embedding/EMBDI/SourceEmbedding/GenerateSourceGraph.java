package main.java.Embedding.EMBDI.SourceEmbedding;

import main.java.Embedding.abstruct_Graph.ConcreteEdgesGraph;
import main.java.Embedding.abstruct_Graph.Graph;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用于建立数据源的embedding的三分图，分为平面上的<tuple, a(i), Si>,以及竖平面上的<a(i), a(ij), Ai>
 */
public class GenerateSourceGraph implements Serializable{
    private static final long serialVersionUID = 1111236;

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
        return graph.set(node1, node2, 3);
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
            // 文件操作所需变量
            String strPro = null;
            // 存储str读取的一行的值
            String[] dataPro = null;
            // 首先读取一行，作为属性
            strPro = br.readLine();
            dataPro = strPro.split(",");
            // 存储全部属性
            sourceGraph.column_i.addAll(Arrays.asList(dataPro));
            for(String s : sourceGraph.column_i){
                // store attribute into sourceGraph
                sourceGraph.addVertex(s);
                sourceGraph.all_nodes.add(s);
            }
            fd.close();
            br.close();
            // 按顺序读取文件
            int column;
            // FIXME:读取当前文件,是第i个文件，则有：
            int fileNum = 0;
            // 判断是否是最后一个文件
            int truthFlag = 0;

            for(String files: fileList){
                FileReader fr = new FileReader(files);
                BufferedReader newBr = new BufferedReader(fr);
                // 每个文件是不同的数据源
//                String source_id = "source_" + fileNum;
//                sourceGraph.addVertex(source_id);
//                sourceGraph.all_nodes.add(source_id);
                // fixme : source_id已经确定,可以分配权重
                int source_value = 1;

                // 从第二行开始处理
                // process null using avg
                // double[] current_sum = new double[column_i.size()];
                int row_id = 0;
                // 判断是否是最后一个文件，即真值文件，如果是，就进行标记
                if(fileList.get(fileList.size()-1).equals(files)){
                    truthFlag = 1;
                }
                String str = null;
                String[] data = null;
                while((str=newBr.readLine())!=null){
                    // 每一行是不同的tuple和row_id
                    // 为每个数据源的表分配row的id
                    column = 0;
                    List<String> row_i = new ArrayList<>();
                    // add node row_id
                    // row_1代表第一个元组
                    // row_1_s1来自第1个数据源的第一行，来自真值
                    // String Ri = "row_" + row_id + "_s" + fileNum;
                    String Ri;
                    if(truthFlag == 0){
                        Ri = "row_" + row_id;
                    }
                    else{
                        // fixme : truthFile name modify!!!
                        Ri = "row_" + row_id + "_s" + fileNum;
                    }

                    // set 互斥
                    sourceGraph.RID_set.add(Ri);

                    sourceGraph.addVertex(Ri);
                    if(!sourceGraph.all_nodes.contains(Ri)){
                        sourceGraph.all_nodes.add(Ri);
                    }
                    row_id++;
                    // add -1
                    data = str.split(",",-1);
                    // FIXME:假设表中的数值都是数字类型，现在判断是否为空
                    for(int index = 0;index<data.length;index++){
                        if(data[index]==null||data[index].equals("")){
                            data[index] = "NEG";
                        }
                    }
                    // row_i暂存每行的数据
                    row_i.addAll(Arrays.asList(data));
                    String[] value = null;
                    List<String> value_i = new ArrayList<>();
                    // 整体添加
                    StringBuilder wholeWord = new StringBuilder();
                    for(String part:row_i){
                        wholeWord.append(part);
                    }
                    // 增加了单元格短语danYG
                    String danYG = wholeWord.toString();
                    sourceGraph.addVertex(danYG);
                    sourceGraph.all_nodes.add(danYG);
                    sourceGraph.addEdge(danYG, Ri);
                    //  仅在此处用到column
                    sourceGraph.addEdge(danYG, sourceGraph.column_i.get(column));


                    // 处理多值
                    for(String Vk:row_i ){
                        // fixme : dart, 包括空格(词语，语义层面)和分号(多值),同时出现空格和分号怎么处理
                        if(Vk.contains(";")) {
                            value = Vk.split(";");
                            // 对于这个属性的成员,每个word储存到value_i中
                            value_i.addAll(Arrays.asList(value));
                            for (String word : value_i) {
                                sourceGraph.addVertex(word);
                                sourceGraph.all_nodes.add(word);
                                sourceGraph.addEdge(word, Ri);
                                //  仅在此处用到column
                                sourceGraph.addEdge(word, sourceGraph.column_i.get(column));
                            }
                        }
                        else if(Vk.contains(" ")){
                            value = Vk.split(" ");
                            // 对于这个属性的成员,每个word储存到value_i中
                            value_i.addAll(Arrays.asList(value));
                            for (String word : value_i) {
                                // G.addNode(word)
                                sourceGraph.addVertex(word);
                                sourceGraph.all_nodes.add(word);
                                // 计算value的点权
                                sourceGraph.addEdge(word, Ri);
                                //  仅在此处用到column
                                sourceGraph.addEdge(word, sourceGraph.column_i.get(column));
                            }
                        }
                        else{
                            sourceGraph.addVertex(Vk);
                            sourceGraph.all_nodes.add(Vk);
                            // 将数值和属性连接
                            sourceGraph.addEdge(Vk,sourceGraph.column_i.get(column));
                            sourceGraph.addEdge(Vk,Ri);
                        }
                        column++;
                    }

                    // fixme clear
                    row_i.clear();
                }
                newBr.close();
                fr.close();
                fileNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // rename path use dataset name and number of sources
        // fixme : CTD中增加version字段
        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
        File f = new File(graphPath);
        try {
            f.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(f);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(sourceGraph);
            outputStream.close();
            System.out.println("Graph is saved");
        } catch (IOException e) {
            System.out.println("graph saving encounters error");
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
