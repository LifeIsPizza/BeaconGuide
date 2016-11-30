package bfs_app;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
    
    //Ritorna il percorso in forma di lista
    public List<Integer> getPathNames(){
        List<Integer> nodesName = null;
        for (Iterator<Node> it = path.iterator(); it.hasNext();) {
            nodesName.add(it.next().getName());
        }
        return nodesName;
    }
    //Update dell'oggetto
    public void setNewMin(int numMin, LinkedList<Node> minPath, int costo){
        numNodes = numMin;
        path = new LinkedList<>(minPath);
        cost = costo;
    }
    
    public int getNext(){
        return path.get(1).getName();
    }
}
