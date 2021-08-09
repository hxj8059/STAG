import java.util.ArrayList;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Look {

    ArrayList<Graph> entities;
    Player currentPlayer;
    String stringToPrint;

    public Look(Player currentPlayer, ArrayList<Graph> entities){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
    }

    public String parseAndGetString(){
        Graph location = currentPlayer.getCurrentLocation();
        Graph locations = entities.get(0).getSubgraphs().get(1);
        stringToPrint = "You are in " +  location.getNodes(false).get(0).getAttribute("description");
        ArrayList<Graph> subGraphs = location.getSubgraphs();
        stringToPrint += "\nYou can see:";
        for(Graph g1:subGraphs){
            //the entities in the current location
            ArrayList<Node> items = g1.getNodes(false);
            //String add category name
            stringToPrint += "\n" + g1.getId().getId() + ":";
            for(Node item:items){
                //String add id and description
                stringToPrint += "\n" +item.getId().getId() + " - " + item.getAttribute("description");
            }
        }
        stringToPrint += "\nYou can access from here:";
        ArrayList<Edge> edges = locations.getEdges();
        for (Edge e : edges){
            //lists the paths to other locations
            if(e.getSource().getNode().getId().getId().equals(location.getNodes(false).get(0).getId().getId())){
                stringToPrint += "\n" + e.getTarget().getNode().getId().getId();
            }
        }
        return stringToPrint;
    }


}
