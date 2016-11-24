/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfs_app;

import java.util.ArrayList;
//import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vigna
 */
public class Node {
    private final int name;
    private final List<Node> viciniNode;
    //private final List<Node> viciniNodeHand;
    private final List<Edge> viciniEdges;
    //private final List<Edge> viciniEdgesHand;
    //private int indice;
    
    public Node(int name){
        this.name = name; 
        viciniNode = new ArrayList<>();
        viciniEdges = new ArrayList<>();
        //viciniNodeHand = new ArrayList<>();
        //viciniEdgesHand = new ArrayList<>();
    }
    
    public int getName(){
        return name;
    }
    
    public void addVicino(Node vicinoNode, Edge vicinoEdge, int orientation){
        /*if (handicap == 2){
            viciniNodeHand.add(vicinoNode);
            viciniEdgesHand.add(vicinoEdge);
        }
        if (handicap == 1){
            viciniNode.add(vicinoNode);
            viciniNodeHand.add(vicinoNode);
            viciniEdges.add(vicinoEdge);
            viciniEdgesHand.add(vicinoEdge);
        }
        if (handicap == 0){
            viciniNode.add(vicinoNode);
            viciniEdges.add(vicinoEdge);
        }*/
        viciniNode.add(vicinoNode);
        viciniEdges.add(vicinoEdge);
    }
    
    
    //gets
    public List<Node> getViciniNode(){
        /*if (handicap == 1)
            return viciniNodeHand;
        else*/
        return viciniNode;
    }
    /*public List<Edge> getViciniEdge(int handicap){
         if (handicap == 1)
            return viciniEdgesHand;
        else
            return viciniEdges;
    }*/
}
