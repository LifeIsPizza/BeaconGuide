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
import java.util.LinkedList;
import java.util.List;


public class Search
{
    private final Graph g;
    private final LinkedList<Node> visited;
    
    //Crei classe search quando hai già creato il grafo
    
    
    //Poi quando ti serve il percorso tra nodo di partenza ed 
    //il nodo di arrivo fai, getNext(src, dst)
    
    public Search(Graph g){
        this.g = g;
        visited = new LinkedList();
    }
    
    public int getNext(int src, int dst){
        int index = g.getNodesNames().indexOf(src);
        Node start = g.getNodes().get(index);
        visited.add(start);
        PathMinimo minimo = new PathMinimo(visited);
        minimo.setNewMin(Integer.MAX_VALUE, visited, Integer.MAX_VALUE);
        depthFirst(g, visited, minimo, 0, src, dst);
        return minimo.getNext();
    }
    /*private static final int START = 101;
    private static final int END = 103;*/
    //private static final int HANDICAP = 1;

    /*public static void main(String[] args) {
        // this graph is NOT directional
        
        Graph g = new Graph();
        g.addLink(101,102,12,5);
        g.addLink(102,101,192,5);
        g.addLink(102,103,260,5);
        /*g.addLink("Fuori Aula A","Scale",12,5);
        
        //0 = is not handicap
        //1 = is handicap
    }*/

    private void depthFirst(Graph graph, LinkedList<Node> visited, PathMinimo minimo, int cost, int src, int dst) {
        int index = graph.getNodesNames().indexOf(visited.getLast().getName());
        Node actual = graph.getNodes().get(index);
        List<Node> nodes = actual.getViciniNode();
        //LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        for (Node node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.getName() == dst) {
                visited.add(node);
                
                //if (visited.size()<minimo.getNumNodes() )
                if (cost < minimo.getCost())
                {
                    minimo.setNewMin(visited.size(), visited, cost);
                }
                System.out.println(printPath(visited));
                visited.removeLast();
                break;
            }
        }
        for (Node node : nodes) {
            if (visited.contains(node) || node.getName()==dst) {
                continue;
            }
            visited.addLast(node);
            //List<Edge>edges =  node.getViciniEdge();
            Edge edge = graph.getEdge(actual, node);
            if (edge != null)
                depthFirst(graph, visited, minimo, cost+edge.getCost(), src, dst);
            visited.removeLast();
        }
    }

    public static String printPath(LinkedList<Node> visited) {
        String print = "";
        for (Node node : visited) {
            print+=(node.getName());
            if (!visited.isEmpty())
            {
                print+=(",");
            }
        }
        return print;
    }
}
