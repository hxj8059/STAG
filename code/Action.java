import java.io.*;
import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Action {
    ArrayList<Graph> entities;
    JSONObject actions;
    Player currentPlayer;
    String stringToPrint;
    String command;
    Graph location;
    JSONObject targetAction;

    public Action(Player currentPlayer, ArrayList<Graph> entities, JSONObject actions, String command){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
        this.actions = actions;
        this.command = command;
    }

    public String parseAndGetString(){
        stringToPrint = "";
        location = currentPlayer.getCurrentLocation();
        targetAction = isActionExist();
        //if action not exist
        if(targetAction == null) {
            stringToPrint = "Not supported action! ";
            return stringToPrint;
        }
        //is subject exist
        if(!isSubjectExist()){
            stringToPrint = "Subjects needed! ";
            return stringToPrint;
        }
        //consume if false health runs out
        if(consumeSubject() == false){
            stringToPrint = "You lose :-(, and lost all your inventory. You respawn at the start location!\n";
            return stringToPrint;
        }
        //produce
        produceSubject();
        return stringToPrint;
    }

    public JSONObject isActionExist(){
        for(Object actionsContents:actions.values()){
            //get action array
            JSONArray actionsContent = (JSONArray) actionsContents;
            for(Object action:actionsContent){
                //get triggers array by key
                JSONArray triggers = (JSONArray) ((JSONObject)action).get("triggers");
                for(Object trigger:triggers){
                    String currentTrigger = (String) trigger;
                    if(command.contains(currentTrigger)){
                        return (JSONObject) action;
                    }
                }
            }
        }
        return null;
    }

    public boolean isSubjectExist(){
        int cnt = 0;
        ArrayList<Graph> subGraphs = location.getSubgraphs();
        JSONArray subjects = (JSONArray) targetAction.get("subjects");
        for(Object subject:subjects){
            String targetSubject = (String) subject;
            //is subject in locations
            for(Graph g1:subGraphs){
                ArrayList<Node> items = g1.getNodes(false);
                for(Node item:items){
                    if (targetSubject.equals(item.getId().getId())) {
                        cnt++;
                    }
                }
            }
            //is subject in inventory
            if(currentPlayer.isInventoryExist(targetSubject)){
                cnt++;
            }
        }
        //if the subject matching count equals subject amount, return true
        if(cnt >= subjects.size()){
            return true;
        }
        return false;
    }

    public boolean consumeSubject(){
        JSONArray consumes = (JSONArray) targetAction.get("consumed");
        ArrayList<Graph> subGraphs = location.getSubgraphs();
        Node itemToDelete = null;
        boolean artefactsExist = false;
        for(Object consume:consumes){
            String consumeItem = (String) consume;
            //health
            if(consumeItem.toLowerCase().contains("health")){
                currentPlayer.consumeHealth();
                stringToPrint += "Health -1 \n";
                if(currentPlayer.health <= 0){
                    //lost all inventory and put in locations
                    for(Node inv:currentPlayer.getInventory()){
                        for(Graph items:subGraphs) {
                            System.out.println(items.getId().getId());
                            //artefacts exists
                            if (items.getId().getId().equals("artefacts")) {
                                ArrayList<Node> artefacts = items.getNodes(true);
                                System.out.println("add" + inv);
                                artefactsExist = true;
                                artefacts.add(inv);
                            }
                        }
                        // artefacts not exist, create artefact category
                        if(artefactsExist == false){
                            Id newId = new Id();
                            newId.setId("artefacts");
                            Graph newGraph = new Graph();
                            newGraph.setId(newId);
                            newGraph.setType(2);
                            newGraph.addNode(inv);
                            subGraphs.add(newGraph);
                        }
                    }
                    //clear the player's inventory
                    currentPlayer.getInventory().clear();
                    Graph spawnLocation = entities.get(0).getSubgraphs().get(0).getSubgraphs().get(0);
                    currentPlayer.setCurrentLocation(spawnLocation);
                    currentPlayer.setHealth(3);
                    Goto newGoto = new Goto(currentPlayer, entities, command);
                    newGoto.movePlayerToLocation(location, spawnLocation, currentPlayer.getName());
                    return false;
                }
            }
            //inventory
            else if(currentPlayer.consumeInventoryItem(consumeItem)){
                stringToPrint += "consume inventory " + consumeItem + "\n";
            }
            //artefacts
            else{
                for(Graph g1:subGraphs){
                    String type = g1.getId().getId();
                    ArrayList<Node> items = g1.getNodes(true);
                    for(Node item:items){
                        if(consumeItem.equals(item.getId().getId())){
                            itemToDelete = item;
                            stringToPrint += "consume " + type + " " + item.getId().getId()+ "\n";
                        }
                    }
                    if(itemToDelete != null) items.remove(itemToDelete);
                }
            }
        }
        return true;
    }

    public void produceSubject(){
        JSONArray productions = (JSONArray) targetAction.get("produced");
        ArrayList<Graph> allLocation = entities.get(0).getSubgraphs().get(0).getSubgraphs();

        for(Object production:productions){
            String produceItem = (String) production;
            //health
            if(produceItem.toLowerCase().contains("health")){
                currentPlayer.produceHealth();
                stringToPrint += "Health +1"+ "\n";
            }
            //locations and unplaced
            else{
                produceLocationAndUnplaced(allLocation, produceItem);
            }
        }
        stringToPrint += targetAction.get("narration");
    }

    public void produceEdge(Node src, Node des){
        Graph edges = entities.get(0).getSubgraphs().get(1);
        PortNode portSec = new PortNode(src);
        PortNode portDes = new PortNode(des);
        Edge newEdge = new Edge(portSec, portDes, 2);
        edges.addEdge(newEdge);
    }

    public void produceLocationAndUnplaced(ArrayList<Graph> allLocation, String produceItem){
        for(Graph newLocation:allLocation){
            // if location
            if(produceItem.equals(newLocation.getNodes(true).get(0).getId().getId())){
                produceEdge(location.getNodes(false).get(0), newLocation.getNodes(false).get(0));
                return;
            }

        }
        for(Graph newLocation:allLocation){
            //if unplaced
            if(newLocation.getNodes(true).get(0).getId().getId().equals("unplaced")){
                moveItemFromUnplaced(newLocation, produceItem);
            }
        }
    }

    public void moveItemFromUnplaced(Graph unplacedLocation, String produceItem){
        ArrayList<Graph> unplacedItems = unplacedLocation.getSubgraphs();
        Node itemToMove = null;
        String itemType = null;
        for(Graph g1:unplacedItems){
            ArrayList<Node> items = g1.getNodes(true);
            for(Node item:items){
                if(produceItem.equals(item.getId().getId())){
                    itemToMove = item;
                    itemType = g1.getId().getId();
                }
            }
            //remove from unplaced
            items.remove(itemToMove);
        }
        //if no such item in unplaced location
        if(itemToMove == null){
            stringToPrint += "Error: no such item in unplaced location: " + produceItem + "\n";
            return;
        }
        ArrayList<Graph> currentItems = location.getSubgraphs();
        for(Graph currentItem:currentItems){
            //if the category is already exist, add directly
            if(itemType.equals(currentItem.getId().getId())){
                ArrayList<Node> items = currentItem.getNodes(true);
                items.add(itemToMove);
                return;
            }
        }
        //if the category not exist, create new category
        Id newId = new Id();
        newId.setId(itemType);
        Graph newGraph = new Graph();
        newGraph.setId(newId);
        newGraph.setType(2);
        newGraph.addNode(itemToMove);
        currentItems.add(newGraph);
    }
}
