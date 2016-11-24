/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfs_app;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;

/**
 *
 * @author vigna
 */
//REMEBER COSTI PERCORSO:
//Si prediligono i percorsi a costo minore, perciò quelli normali preferiscono le scale all'ascensore
//quindi, le scale avranno costo 1 mentre l'ascensore avrà costo 2

public class Graph {
    private final List<Node> nodes;
    private final List<Integer> nodesName;
    private final List<Edge> edges;
   
    public Graph(){
        nodes = new ArrayList<>();
        nodesName = new ArrayList<>();
        edges = new ArrayList<>();
    }
    
    
    public void addLink(int src, int dst, int orientation, int cost){
        
        Node src_node;
        Node dst_node;
        int index;
        
        //if (nodesName.isEmpty())
        //{
            if (nodesName.contains(src) == true)
            {
                index = nodesName.indexOf(src);
                src_node = nodes.get(index);
            }
            else
            {
                nodesName.add(src);
                src_node = new Node(src);
                nodes.add(src_node);
            }
            if (nodesName.contains(dst) == true)
            {
                index = nodesName.indexOf(dst);
                dst_node = nodes.get(index);
            }
            else
            {
                nodesName.add(dst);
                dst_node = new Node(dst);
                nodes.add(dst_node);
            }
        /*}
        else {
            nodesName.add(src);
            src_node = new Node(src);
            nodes.add(src_node);
            nodesName.add(dst);
            dst_node = new Node(dst);
            nodes.add(dst_node);
        }*/
        
        //handicap 0,1,2
        /*if (handicap > 0)
            cost = 10;
        else
            cost = 5;*/
        Edge newEdge = new Edge(src_node, dst_node, orientation, cost);
        edges.add(newEdge);
        src_node.addVicino(dst_node, newEdge, orientation);
        //dst_node.addVicino(src_node, newEdge, orientation);
    }
    
    public List getNodesNames(){
        return nodesName;
    }
  
    public List<Node> getNodes(){
        return nodes;
    }
    
    public List getEdges(){
        return edges;
    }   
    //Get edge between src and dst
    public Edge getEdge(Node src, Node dst){
        for (Edge edge : edges) 
            if ((edge.getSrc() == src) && (edge.getDst() == dst) || (edge.getSrc() == dst) && (edge.getDst() == src) )
                return edge;
        return null;
    }   
    
    @Override
    public String toString() {
        String solutionString = "\n";
        solutionString = this.nodes.stream().map((node) -> node.getName() + "\n").reduce(solutionString, String::concat);
        return solutionString;
    }
     
}
