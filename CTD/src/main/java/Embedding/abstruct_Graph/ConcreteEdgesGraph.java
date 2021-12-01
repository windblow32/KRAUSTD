/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package main.java.Embedding.abstruct_Graph;

import java.io.Serializable;
import java.util.*;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph<L> implements Graph<L>, Serializable {
    private static final long serialVersionUID = 111112;
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();
    
    // Abstraction function:
    // AF(vertices)=图中的点
    // AF(edges)=图中的边
    // Representation invariant:
    // edge长度的weight必须是大于0的，两端的点在vertices中，有起始的点
    // vertex点必须在vertices中
    // Safety from rep exposure:
    // vertices和edges设置为private防止泄露，
    // 由于要求vertices和edges是mutable，所以返回时候用defensive copies,new
    
    // TODO constructor
    public ConcreteEdgesGraph(){

    }
    // TODO checkRep
    private void checkRep(){
//        for(Edge<L> edge : edges){
//            assert vertices.contains(edge.getSource());
//            assert vertices.contains(edge.getTarget());
//            assert edge.getWeight()>0;
//        }
    }

    @Override
    public boolean add(L vertex) {
        if(vertices.contains(vertex)){
            // fixme:顶点重复加入提示忽略
            //System.out.println("重复加入顶点\n");
            return false;
        }
        // checkRep();
        return vertices.add(vertex);
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
    @Override
    public int set(L source, L target, int weight) {
        int preweight=0;
        if(weight<0) {
            System.out.println("权值为负！\n");
            return -1;
        }
        else {
            if(weight>0) {
                Iterator<Edge<L>> edgeIterator=edges.iterator();
                while(edgeIterator.hasNext()) {  //当新边已经存在时，除去此边
                    Edge<L> a=edgeIterator.next();
                    if(a.getSource().equals(source) && a.getTarget().equals(target)) {
                        preweight=a.getWeight();
                        edgeIterator.remove();
                        break;
                    }
                }
                vertices.add(source);  //添加新边
                vertices.add(target);
                Edge<L> edge=new Edge<L>(source,target,weight);
                edges.add(edge);
            }
            if(weight==0) {  //新边权值为0且已经存在时除去此边
                Iterator<Edge<L>> him=edges.iterator();
                while(him.hasNext()) {
                    Edge<L> m=him.next();
                    if (m.getSource().equals(source) && m.getTarget().equals(target)) {
                        him.remove();
                        preweight=m.getWeight();
                        break;
                    }
                }
            }
        }
        // checkRep();
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
    @Override
    public boolean remove(L vertex) {
        if(vertices.contains(vertex)){
            vertices.remove(vertex);
            Iterator<Edge<L>> edgeIterator = edges.iterator();
            while(edgeIterator.hasNext()){
                Edge<L> edge = edgeIterator.next();
                if(edge.getSource().equals(vertex)||edge.getTarget().equals(vertex)){
                    edgeIterator.remove();
                }
            }
        }
        else {
            //这点不存在
            return false;
        }
        // checkRep();
        return true;
    }
    /**
     * Get all the vertices in this graph.
     *
     * @return the set of labels of vertices in this graph
     */
    @Override public Set<L> vertices() {
        return new HashSet<L>(vertices);
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
        Iterator<Edge<L>> edgeIterator = edges.iterator();
        Map<L, Integer> source_map = new HashMap<>();
        while (edgeIterator.hasNext()){
            Edge<L> edge = edgeIterator.next();
            if(edge.getTarget().equals(target)){
                source_map.put(edge.getSource(), edge.getWeight());
            }
        }
        // checkRep();
        return source_map;
    }
    
    @Override public Map<L, Integer> targets(L source) {
        Iterator<Edge<L>> edgeIterator = edges.iterator();
        Map<L, Integer> target_map = new HashMap<>();
        while(edgeIterator.hasNext()){
            Edge<L> edge = edgeIterator.next();
            if(edge.getSource().equals(source)){
                target_map.put(edge.getTarget(), edge.getWeight());
            }
        }
        // checkRep();
        return target_map;
    }
    
    // toString()
    @Override
    public String toString(){
        StringBuilder mm = new StringBuilder();
        for (Edge<L> p : edges) {
            mm.append(p.toString());
        }
        return mm.toString();
    }
    
}

/**
 * TODO specification
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge<L> implements Serializable{
    private static final long serialVersionUID = 111113;
    
    // fields
    private L source;
    private L target;
    private int weight;

    // Abstraction function:
    // AF(vertices)=图中的点
    // AF(edges)=图中的边
    // Representation invariant:
    // edge长度的weight必须是大于0的，两端的点在vertices中，有起始的点
    // vertex点必须在vertices中
    // Safety from rep exposure:
    // vertices和edges设置为private防止泄露，
    // 由于要求vertices和edges是mutable，所以返回时候用defensive copies,new

    // construct
    /**
     * 初始化两个点和这个边的权值
     * @param source 边的一个点
     * @param target 边的另一个终点
     * @param weight 边的权重*/
    public Edge(L source,L target, int weight){
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }
    // checkRep

    /**
     * 检查不变性mutable
     */
    public void checkRep(){
//        assert source!=null;
//        assert target!=null;
//        assert weight>=0;
    }

    // methods

    /**
     *
     * @return source点
     */
    public L getSource(){
        return source;
    }

    /**
     *
     * @return target点
     */
    public L getTarget(){
        return target;
    }

    /**
     *
     * @return 边的权重weight
     */
    public int getWeight(){
        // checkRep();
        return weight;
    }

    /**
     *
     * @return 描述这个类Edge的方式
     */
    @Override
    public String toString(){
        return source.toString() + "->" + target.toString() + " 边长为：" + weight + "\n";

    }
}
