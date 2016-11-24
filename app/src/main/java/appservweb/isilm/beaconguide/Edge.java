/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appservweb.isilm.beaconguide;

/**
 *
 * @author vigna
 */
public class Edge {
    private final Node src;
    private final Node dst;
    private final int cost;
    private final int orientation;
    
    public Edge(Node src, Node dst, int orientation ,int cost){
        this.src = src;
        this.dst = dst;
        this.cost = cost;
        this.orientation = orientation;
        /*if (handicap >=0)
            this.handicap = 1;
        else
            this.handicap = 0;*/
    }
    
    public Node getSrc(){
        return src;
    }
    public Node getDst(){
        return dst;
    }
    public int getCost(){
        return cost;
    }
    public int getOrientation(){
        return orientation;
    }
    
}
