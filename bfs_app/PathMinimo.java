/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfs_app;

import java.util.LinkedList;

/**
 *
 * @author vigna
 */
public class PathMinimo {
    
    private int numNodes;
    private LinkedList<Node> path;
    private int cost;
    
    public PathMinimo(LinkedList<Node> minPath){
        this.cost = Integer.MAX_VALUE;
        this.path = new LinkedList<>();
        numNodes = 0;
    }
    
    public int getNumNodes(){
        return numNodes;
    }
    
    public int getCost(){
        return cost;
    }
    
    public LinkedList<Node> getPath(){
        return this.path;
    }
    
    public void setNewMin(int numMin, LinkedList<Node> minPath, int costo){
        numNodes = numMin;
        path = new LinkedList<>(minPath);
        cost = costo;
    }
    
    public int getNext(){
        return path.get(1).getName();
    }
}
