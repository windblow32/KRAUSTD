/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package abstruct_Graph;

import java.util.*;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph<L> implements Graph<L> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();

    // Abstraction function:
    // AF(vertices)={图中所有的vertices[i]|0<=i<vertices.sizes()}
    // Representation invariant:
    // vertices中不能有重复点
    // Safety from rep exposure:
    // 设置vertices为private
    // 由于vertices是mutable，所以在返回他们时进行了defensive copies
    
    // TODO constructor
    public ConcreteVerticesGraph(){

    }
    // TODO checkRep
    private void checkRep(){
        Set<Vertex<L>> testvertice = new HashSet<>(vertices);
        assert testvertice.size()==vertices.size();
    }
    /**
     * Add a vertex to this graph.
     *
     * @param vertex label for the new vertex
     * @return true if this graph did not already include a vertex with the
     *         given label; otherwise false (and this graph is not modified)
     */
    @Override public boolean add(L vertex) {
        for(Vertex<L> vex:vertices){
            if(vex.getVextex().equals(vertex)){
                return false;
            }
        }
        Vertex<L> new_vex = new Vertex(vertex);
        vertices.add(new_vex);
        checkRep();
        return true;
    }
    /**
     * Add, change, or remove a weighted directed edge in this graph.
     * If weight is nonzero, add an edge or update the weight of that edge;
     * vertices with the given labels are added to the graph if they do not
     * already exist.
     * If weight is zero, remove the edge if it exists (the graph is not
     * otherwise modified).
     *
     * @param source label of the source vertex
     * @param target label of the target vertex
     * @param weight nonnegative weight of the edge
     * @return the previous weight of the edge, or zero if there was no such
     *         edge
     */
    @Override public int set(L source, L target, int weight) {
        int preweight=0;
        this.add(source);
        this.add(target);// 如果重复就返回假，没影响
        for(Vertex<L> vex:vertices){
            // 一个点要不在source中，必然在target中
            if(vex.getVextex().equals(source)){
                // sourcemap中应该有，找weight，然后addtarget就可以了
                preweight = vex.addtarget(target,weight);
            }
            if(vex.getVextex().equals(target)){
                preweight = vex.addsource(source, weight);
            }
        }
        checkRep();
        return preweight;
    }
    /**
     * Remove a vertex from this graph; any edges to or from the vertex are
     * also removed.
     *
     * @param vertex label of the vertex to remove
     * @return true if this graph included a vertex with the given label;
     *         otherwise false (and this graph is not modified)
     */
    @Override public boolean remove(L vertex) {
        Iterator<Vertex<L>> iterator = vertices.iterator();
        while(iterator.hasNext()){
            Vertex<L> vex = iterator.next();
            // 找到了它本身
            if(vex.getVextex().equals(vertex)){
                // 找到了相同名字的
                iterator.remove();
                checkRep();
                return true;
            }
            // 删除它关联的其他边
            else{
                if(vex.getSource_map().containsKey(vertex)){
                    vex.removesource(vertex);
                }
                if(vex.getTarget_map().containsKey(vertex)){
                    vex.removetarget(vertex);
                }
            }
        }
        checkRep();
        return false;
    }
    
    @Override public Set<L> vertices() {
        Set<L> vexset = new HashSet<>();
        for(Vertex<L> vex:vertices){
            vexset.add(vex.getVextex());
        }
        checkRep();
        return vexset;
    }
    /**
     * Get the source vertices with directed edges to a target vertex and the
     * weights of those edges.
     *
     * @param target a label
     * @return a map where the key set is the set of labels of vertices such
     *         that this graph includes an edge from that vertex to target, and
     *         the value for each key is the (nonzero) weight of the edge from
     *         the key to target
     */
    @Override public Map<L, Integer> sources(L target) {
        Map<L,Integer> temp = new HashMap<>();
        for(Vertex<L> vex:vertices){
            if(vex.getVextex().equals(target)){
                // 找到了这个点
                temp = vex.getSource_map();
                break;
            }
        }
        Map<L, Integer> source_map = new HashMap<L,Integer>(temp);
        checkRep();
        return source_map;
    }
    
    @Override public Map<L, Integer> targets(L source) {
        Map<L,Integer> temp = new HashMap<>();
        for(Vertex<L> vex: vertices){
            if(vex.getVextex().equals(source)){
                temp = vex.getTarget_map();
                break;
            }
        }
        checkRep();
        return temp;
    }
    
    // toString()
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(Vertex<L> vex:vertices){
            str.append(vex.toString());
        }
        return str.toString();
    }
    
}

/**
 * TODO specification
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex<L> {


    private final L vextex;  //储存顶点的名字
    private final Map<L,Integer> source_map; //储存该顶点的源顶点及边的长度的源点Map
    private final Map<L,Integer> target_map; //储存该顶点终点及边的长度的终点Map
    // Abstraction function:
    // AF(mark)=点的名字
    // AF(allsource)=指向这个点的所有点和边
    // AF(alltarget)=这个点指向的所有点和边
    // Representation invariant:
    // 每个边的权值应该大于0
    // Safety from rep exposure:
    // 将mark,allsource,alltarget设置为private
    
    // TODO constructor
    public Vertex(L vextex){
        this.vextex = vextex;
        this.source_map = new HashMap<>();
        this.target_map = new HashMap<>();
    }
    // checkRep
    private void checkRep(){
        Set<L> point1 = source_map.keySet();
        if(point1!=null){
            Iterator<L> iterator = point1.iterator();
            while(iterator.hasNext()){
                L key = iterator.next();
                Integer value = source_map.get(key);
                assert value>0;
            }
        }
        Set<L> point2 = target_map.keySet();
        if(point2!=null){
            Iterator<L> iterator = point2.iterator();
            while(iterator.hasNext()){
                L key = iterator.next();
                Integer value = target_map.get(key);
                assert value>0;
            }
        }
    }
    // methods
    /**
     * @return 点的名字
     */
    public L getVextex(){
        return vextex;
    }
    public Map<L ,Integer> getSource_map(){
        return new HashMap<L,Integer>(source_map);
    }
    public Map<L,Integer> getTarget_map(){
        return new HashMap<L,Integer>(target_map);
    }

    /**
     * 在源点Map中加入某源点，若weight不为0，则将其加入source中(若源点已存在，则更新其weight并返回原weight，不存在则直接构建新点并返回0)
     * 若weight为0，则移除源点(不存在返回0，存在返回原weight)
     * @param newsource 待加入的源点
     * @param weight  源点到此点的长度
     * @return 旧的边长，没有则返回0
     */
    public int addsource(L newsource,int weight){
        Integer preweight = 0;
        if(weight > 0){
            preweight = source_map.put(newsource, weight);
            if(preweight==null){
                preweight = 0;
            }
        }
        if(weight==0){
            //移除
            preweight=this.removesource(newsource);
        }
        if(weight<0) {
            System.out.println("权值为负");
            return -1;
        }
        checkRep();
        return preweight;
    }
    /**
     * 在终点Map中加入某终点，若weight不为0，则将其加入target中(若终点已存在，则更新其weight并返回原weight，不存在则直接构建新点并返回0)
     * 若weight为0，则移除终点(不存在返回0，存在返回原weight)
     * @param newtarget 待加入的终点
     * @param weight  终点到此点的长度
     * @return 旧的边长，没有则返回0
     */
    public int addtarget(L newtarget, int weight){
        Integer preweight = 0;
        if(weight>0){
            preweight = target_map.put(newtarget, weight);
            if(preweight==null){
                preweight = 0;
            }
        }
        if(weight==0){
            preweight = this.removetarget(newtarget);
        }
        if(weight<0){
            System.out.println("权值为负！");
            return -1;
        }
        checkRep();
        return preweight;
    }

    /**
     * 在源点表中删除某起始点，并返回旧的边长
     * @param newsource 某起始点
     * @return  旧的边长，没有则返回0
     */
    public int removesource(L newsource){
        Integer newweight = source_map.remove(newsource);
        if(newweight==null){
            // 如果没有就返回0
            return 0;
        }
        else{
            //返回旧的weight
            return newweight;
        }

    }

    /**
     * 在终点表中删除某终点，并返回旧的边长
     * @param newtarget 某终点
     * @return  旧的边长，没有则返回0
     */
    public int removetarget(L newtarget){
        Integer newweight = target_map.remove(newtarget);
        if(newweight==null){
            return 0;
        }
        else{
            return newweight;
        }
    }
    // toString()
    /**
     * 用字符串表示点
     */
    @Override
    public String toString(){
        return "顶点名称：" + this.vextex.toString() + ", 指向该点的点的名称与边的权值为：" + this.source_map.toString() + "，它指向的点的名称以及指向它的边的权重为：" + this.target_map.toString() + "\n";

    }
}
