import java.util.*;
import STAGExceptions.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Get extends Command{
    String command;
    String[] splitCommand;

    public Get(Player currentPlayer, ArrayList<Graph> entities, String command){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
        this.command = command;
        splitCommand = command.split(" ");
    }

    public String parseAndGetString(){
        Graph location = currentPlayer.getCurrentLocation();
        ArrayList<Graph> subGraphs = location.getSubgraphs();
        for(Graph items:subGraphs) {
            //can only get artefacts
            if(items.getId().getId().equals("artefacts")){
                ArrayList<Node> artefacts = items.getNodes(true);
                for(Node artefact:artefacts){
                    for(String commandWord:splitCommand){
                        if(commandWord.equals(artefact.getId().getId())){
                            //add to inventory and remove from location
                            currentPlayer.addInventory(artefact);
                            artefacts.remove(artefact);
                            return "picked up "+artefact.getId().getId();
                        }
                    }
                }
            }
        }
        return "Error: the item is not collectable or you do not provide a item?\n";
    }
}
