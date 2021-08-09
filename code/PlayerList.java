import STAGExceptions.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerList {
    List<Player> playerList = new ArrayList<>();

    public Player getPlayerByName(String name) throws NotExistException{
        for(Player player:playerList){
            if(player.getName().equals(name)){
                return player;
            }
        }
        throw new NotExistException(name);
    }

    public void addPlayerByName(String name, ArrayList<Graph> entities){
        Player newPlayer = new Player(name);
        //initialise the start location of a new player
        Graph start = entities.get(0).getSubgraphs().get(0).getSubgraphs().get(0);
        newPlayer.setCurrentLocation(start);
        addPlayerToLocation(start, name);
        newPlayer.setHealth(3);
        playerList.add(newPlayer);
    }

    public boolean isPlayerExists(String name){
        for(Player player:playerList){
            if(player.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public void addPlayerToLocation(Graph start, String name){
        ArrayList<Graph> items = start.getSubgraphs();
        for(Graph item:items){
            //if players exist
            if(item.getId().getId().equals("players")){
                Node newNode = new Node();
                Id playerId = new Id();
                playerId.setId(name);
                newNode.setId(playerId);
                newNode.setAttribute("description",name);
                item.addNode(newNode);
                return;
            }
        }
        //no players
        Id newId = new Id();
        newId.setId("players");
        Graph newGraph = new Graph();
        newGraph.setId(newId);
        newGraph.setType(2);
        Node newNode = new Node();
        Id playerId = new Id();
        playerId.setId(name);
        newNode.setId(playerId);
        newNode.setAttribute("description",name);
        newGraph.addNode(newNode);
        start.getSubgraphs().add(newGraph);
        return;
    }
}
