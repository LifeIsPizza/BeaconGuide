/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfs;

/**
 *
 * @author vigna
 */
import java.util.LinkedList;

public class Search {

    private static final String START = "Ascensore Piano Primo";
    private static final String END = "Bagno Donne";

    public static void main(String[] args) {
        // this graph is directional
        Graph g = new Graph();
        g.addEdge("Fuori Aula A","Scale");
        g.addEdge("Scale","Fuori Aula A");
        g.addEdge("Fuori Aula A","Ascensore Piano Primo");
        g.addEdge("Ascensore Piano Primo","Fuori Aula A");
        g.addEdge("Ascensore Piano Terra","Ascensore Piano Primo");
        g.addEdge("Ascensore Piano Primo","Ascensore Piano Terra");
        g.addEdge("Ascensore Piano Terra","Fuori Aula B");
        g.addEdge("Fuori Aula B","Ascensore Piano Terra");
        g.addEdge("Fuori Aula B","Scale");
        g.addEdge("Scale","Fuori Aula B");
        g.addEdge("Fuori Aula A","Fuori Lab 3");
        g.addEdge("Fuori Lab 3","Fuori Aula A");
        g.addEdge("Fuori Aula B", "Esterno Lab 2");
        g.addEdge("Esterno Lab 2","Fuori Aula B");
        g.addEdge("Esterno Lab 2","Aula C");
        g.addEdge("Aula C","Esterno Lab 2");
        g.addEdge("Esterno Lab 2","Parcheggio");
        g.addEdge("Parcheggio","Esterno Lab 2");
        g.addEdge("Macchinette","Parcheggio");
        g.addEdge("Parcheggio","Macchinette");
        g.addEdge("Esterno Lab 2","Fuori Bagni");
        g.addEdge("Fuori Bagni","Esterno Lab 2");
        
        
        
        g.addEdge("Aula A","Fuori Aula A");
        g.addEdge("Fuori Aula A","Aula A");
        g.addEdge("Fuori Aula B","Aula B");
        g.addEdge("Aula B","Fuori Aula B");
        g.addEdge("Esterno Lab 2","Lab 2");
        g.addEdge("Lab 2","Esterno Lab 2");
        g.addEdge("Fuori Lab 3","Lab 3");
        g.addEdge("Lab 3","Fuori Lab 3");
        g.addEdge("Bagno Uomini","Fuori Bagni");
        g.addEdge("Fuori Bagni","Bagno Uomini");
        g.addEdge("Bagno Donne","Fuori Bagni");
        g.addEdge("Fuori Bagni","Bagno Donne");
        g.addEdge("Macchinette","Fuori Bagni");
        g.addEdge("Fuori Bagni","Macchinette");
        
        LinkedList<String> visited = new LinkedList();
        visited.add(START);
        PathMinimo minimo = new PathMinimo();
        minimo.setNewMin(999, visited);
        new Search().depthFirst(g, visited, minimo);
        //minimo.getPath() contiene la LinkedList del percorso minimo
        System.out.println("Il percorso migliore è: "+printPath(minimo.getPath()));
    }

    private void depthFirst(Graph graph, LinkedList<String> visited, PathMinimo minimo) {
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                if (visited.size()<minimo.getNumNodes() )
                {
                    minimo.setNewMin(visited.size(), visited);
                }
                System.out.println(printPath(visited));
                visited.removeLast();
                break;
            }
        }
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            depthFirst(graph, visited, minimo);
            visited.removeLast();
        }
    }

    public static String printPath(LinkedList<String> visited) {
        String print = "";
        for (String node : visited) {
            print+=(node);
            if (!visited.isEmpty())
            {
                print+=(",");
            }
        }
        return print;
    }
}
