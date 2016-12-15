package appservweb.isilm.beaconguide;

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
        this.path = new LinkedList<>(minPath);
        numNodes = path.size();
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
        if(numNodes <2)
            return path.get(0).getName();
        else
            return path.get(1).getName();
    }
}
