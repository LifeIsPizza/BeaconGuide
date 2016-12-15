package appservweb.isilm.beaconguide;

/**
 *
 * @author vigna
 */
import java.util.LinkedList;
import java.util.List;
import android.util.Log;

//COME UTILIZZARE QUESTA CLASSE:
//Crei classe search DOPO aver già creato il grafo ed aggiunto i collegamenti
    
//Poi quando ti serve il percorso tra nodo di partenza ed il nodo di arrivo fai, 
//getNext(src, dst) per ricevere il prossimo beacon del percorso.
//ALTRIMENTI fai getPath(src,dst) che restituisce il percorso completo
public class Search
{
    private final Graph g;
    private final LinkedList<Node> visited;
    
    
    
    public Search(Graph g){
        this.g = g;
        visited = new LinkedList();
    }
    
    //Ritorno l'id del beacon successivo
    public int getNext(int src, int dst){
        if (src == dst)
            return dst;
        try
        {
            visited.clear();
            int index = g.getNodesNames().indexOf(src);
            Node start = g.getNodes().get(index);
            visited.add(start);
            PathMinimo minimo = new PathMinimo(visited);
            minimo.setNewMin(Integer.MAX_VALUE, visited, Integer.MAX_VALUE);
            Log.d("Search","Minimo inizializzato");
            depthFirst(g, visited, minimo, 0, src, dst);
            Log.d("Search","Ricerca terminata");
            return minimo.getNext();
        }
        catch(Exception exc){
            Log.d("ERROR_Search","Eccezione: "+exc);
            return 0;
        }
    }
    
    //Ritorno una lista con gli id dei beacon successivi
    public List<Integer> getPath(int src, int dst){
        try
        {
            visited.clear();
            int index = g.getNodesNames().indexOf(src);
            Node start = g.getNodes().get(index);
            visited.add(start);
            PathMinimo minimo = new PathMinimo(visited);
            minimo.setNewMin(Integer.MAX_VALUE, visited, Integer.MAX_VALUE);
            Log.d("Search","Minimo inizializzato");
            depthFirst(g, visited, minimo, 0, src, dst);
            Log.d("Search","Ricerca terminata");
            return minimo.getPathNames();
        }
        catch(Exception exc){
            Log.d("ERROR_Search","Eccezione: "+exc);
            return null;
        }
    }

    //INPUT: 
    //Un grafo, la lista dei nodi visitati, un minimo assoluto, 
    //il costo attuale del percorso, nodo di partenza e nodo di arrivo.
    
    //DO:
    //Implementa una BFS che richiama ricorsivamente. 
    //In "minimo" viene memorizzato il percorso migliore
    private void depthFirst(Graph graph, LinkedList<Node> visited, PathMinimo minimo, int cost, int src, int dst) {
        int index = graph.getNodesNames().indexOf(visited.getLast().getName());
        Node actual = graph.getNodes().get(index);
        List<Node> nodes = actual.getViciniNode();
        //LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        for (Node node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            Edge edge = graph.getEdge(actual, node);
            if (edge != null)
                cost +=edge.getCost();
            if (node.getName() == dst) {
                visited.add(node);
                
                //if (visited.size()<minimo.getNumNodes() )
                if (cost < minimo.getCost())
                {
                    minimo.setNewMin(visited.size(), visited, cost);
                }
                cost-=edge.getCost();
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
