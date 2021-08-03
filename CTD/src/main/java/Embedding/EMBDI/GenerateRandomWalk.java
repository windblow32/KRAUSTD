package main.java.Embedding.EMBDI;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateRandomWalk {
    GenerateTripartiteGraph graph = new GenerateTripartiteGraph();

    public GenerateRandomWalk(GenerateTripartiteGraph graph){
        this.graph = graph;
    }
    public String findNeiboringRID(String vertex){
        String node=null;
        Map<String, Integer> neighbor_map = new HashMap<>();
        Set<String> neighbor_set = new HashSet<>();
        if(!graph.vertices().contains(vertex)){
            System.out.println("error! the node is not exist!");
            return null;
        }
        else {
            neighbor_map = new HashMap<>(graph.targets(vertex));
            neighbor_map.putAll(graph.sources(vertex));
            // 所有连接到vertex的点的集合
            neighbor_set = new HashSet<>(neighbor_map.keySet());
            // 从中筛选处符合row_前缀的vertex
            for(String str : neighbor_set){
                Pattern p = Pattern.compile("(row_)(\\d+)");
                Matcher m = p.matcher(str);
                boolean b = m.matches();
                if(!b){
                    neighbor_map.remove(str);
                }
            }
            // 生成随机数index
            if(neighbor_set.size()>0){
                Random rand = new Random();
                int index = rand.nextInt(neighbor_set.size());
                // 返回随机选中的node
                Iterator<String> itor = neighbor_set.iterator();
                while (itor.hasNext()) {
                    node = itor.next();
                    if (index == 0) {
                        break;
                    }
                    index--;
                }
                return node;
            }
            return null;
        }
    }

    /**
     *
     * @param vertex:开始的固定点
     * @return 找和vertex相连的点,然后存储到set中,用random选一个输出
     */
    public String findRandomNeighbor(String vertex){
        String node=null;
        Map<String, Integer> neighbor_map = new HashMap<>();
        Set<String> neighbor_set = new HashSet<>();
        if(!graph.vertices().contains(vertex)){
            System.out.println("error! the node is not exist!");
            return null;
        }
        else{
            neighbor_map = graph.targets(vertex);
            neighbor_map.putAll(graph.sources(vertex));
            // 所有连接到vertex的点的集合
            neighbor_set = neighbor_map.keySet();
            // 生成随机数index
            Random rand = new Random();
            int index = rand.nextInt(neighbor_set.size());
            // 返回随机选中的node
            Iterator<String> itor = neighbor_set.iterator();
            while(itor.hasNext()){
                node = itor.next();
                if(index==0){
                    break;
                }
                index--;
            }
            return node;
        }
    }

    /**
     *
     * @param node:开始游走的点的名字
     * @param length:游走长度
     * @return 游走的序列walk
     */
    public List<String> randomWalk(String node,int length){
        List<String> walkpath = new ArrayList<>();
        String Rj = findNeiboringRID(node);
        walkpath.add(node);
        if(Rj!=null){
            walkpath.add(Rj);
        }
        String currentNode = node;
        String nextNode = null;
        // 初始size就是2,length相当于派生步骤,一共几个点.
        while(walkpath.size()<length){
            nextNode = findRandomNeighbor(currentNode);
            walkpath.add(nextNode);
            currentNode = nextNode;
        }
        return walkpath;
    }
}
