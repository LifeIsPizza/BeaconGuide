/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfs;

import java.util.LinkedList;

/**
 *
 * @author vigna
 */
public class PathMinimo {
    
    private int numNodes;
    private LinkedList<String> path;
    
    public PathMinimo(){
        this.numNodes = 0;
        this.path = new LinkedList<>();
    }
    
    public int getNumNodes(){
        return this.numNodes;
    }
    
    public LinkedList<String> getPath(){
        return this.path;
    }
    
    public void setNewMin(int numMin, LinkedList<String> minPath){
        this.numNodes = numMin;
        this.path = new LinkedList<>(minPath);
    }
}
