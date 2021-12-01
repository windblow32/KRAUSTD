package main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph;



import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateNormalizeDistributeSourceTripartiteRandomWalk {
    GenerateNormalizeDistributeSourceTripartite graph = new GenerateNormalizeDistributeSourceTripartite();

    public GenerateNormalizeDistributeSourceTripartiteRandomWalk(GenerateNormalizeDistributeSourceTripartite graph){
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
        String node = null;
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
     * 利用边权选择相邻边权最小的点进行游走
     * @param vertex:带查找的点
     * @return 选中的邻居结点
     */
    public String findMinNeighbor(String vertex){
        String node = null;
        Map<String, Integer> neighbor_map = new HashMap<>();
        Set<String> neighbor_set = new HashSet<>();
        if(!graph.vertices().contains(vertex)){
            System.out.println("error! the node is not exist!");
            return null;
        }
        else{
            // targets返回值有权重
            neighbor_map = graph.targets(vertex);
            neighbor_map.putAll(graph.sources(vertex));
            // 所有连接到vertex的点的集合
            Iterator<String> keyItor= neighbor_map.keySet().iterator();
            int max = 0;
            // 存储value相同的key
            Set<String> keySet = new HashSet<>();
            while (keyItor.hasNext()){
                String key = keyItor.next();
                int value = neighbor_map.get(key);
                if(value>max){
                    max = value;
                    // 原有的清除
                    keySet.clear();
                    keySet.add(key);
                }
                else if(value==max){
                    keySet.add(key);
                }
            }
            Random r = new Random();
            int index = r.nextInt(keySet.size());
            // 返回随机选中的node
            Iterator<String> itor = keySet.iterator();
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
            nextNode = findMinNeighbor(currentNode);
            walkpath.add(nextNode);
            currentNode = nextNode;
        }
        return walkpath;
    }
}
