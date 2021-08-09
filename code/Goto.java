import java.util.ArrayList;

import STAGExceptions.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Goto extends Command{

    String command;
    String[] splitCommand;
    String newLocation;

    public Goto(Player currentPlayer, ArrayList<Graph> entities, String command){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
        this.command = command;
        splitCommand = command.split(" ");
    }

    public String parseAndGetString() throws NotExistException{
        Graph location = currentPlayer.getCurrentLocation();
        //test is the location available
        if(!isPathAvailable(location)) throw new NotExistException("No such location available!");
        ArrayList<Graph> allLocations = entities.get(0).getSubgraphs().get(0).getSubgraphs();
        for(Graph oneLocation : allLocations){
            //new location id is same as a location in the list
            if(newLocation.equals(oneLocation.getNodes(false).get(0).getId().getId())){
                currentPlayer.setCurrentLocation(oneLocation);
                //move player from old location to new location
                movePlayerToLocation(location, oneLocation, currentPlayer.getName());
                Look look = new Look(currentPlayer, entities);
                return look.parseAndGetString();
            }
        }
        throw new NotExistException("You cannot goto this place");
    }

    public boolean isPathAvailable(Graph location){
        Graph Paths = entities.get(0).getSubgraphs().get(1);
        ArrayList<Edge> edges = Paths.getEdges();
        for (Edge e : edges){
            //lists the paths to other locations
            if(e.getSource().getNode().getId().getId().equals(location.getNodes(false).get(0).getId().getId())){
                for(String commandWord : splitCommand){
                    if(commandWord.equals(e.getTarget().getNode().getId().getId())){
                        newLocation = commandWord;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void movePlayerToLocation(Graph source, Graph destination, String name){
        //delete player from old location
        ArrayList<Graph> oldItems = source.getSubgraphs();
        Node playerToDelete = null;
        for(Graph item:oldItems) {
            //players graph
            if (item.getId().getId().equals("players")) {
                ArrayList<Node> players = item.getNodes(true);
                for (Node player : players) {
                    if (player.getId().getId().equals(name)) {
                       playerToDelete = player;
                    }
                }
                players.remove(playerToDelete);
            }
        }
        //add player to new location
        ArrayList<Graph> items = destination.getSubgraphs();
        for(Graph item:items){
            //if players category exist
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
        //no players, create players category
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
        destination.getSubgraphs().add(newGraph);
        return;
    }
}
