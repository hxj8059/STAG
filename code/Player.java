import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Player {
    protected String name;

    public List<Node> getInventory() {
        return inventory;
    }

    protected List<Node> inventory = new ArrayList<>();
    protected Graph currentLocation;
    protected int health;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void consumeHealth() {
        health--;
    }

    public void produceHealth() {
        health++;
    }


    public Player(String name){
        this.name = name;
    }

    public void addInventory(Node item){
        inventory.add(item);
    }

    public boolean isInventoryExist(String itemId){
        for(Node inv:inventory){
            if(inv.getId().getId().equals(itemId)){
                return true;
            }
        }
        return false;
    }

    public boolean dropInventoryItem(String itemId){
        for(Node inv:inventory){
            if(inv.getId().getId().equals(itemId)){
                ArrayList<Graph> subGraphs = currentLocation.getSubgraphs();

                for(Graph items:subGraphs) {
                    if (items.getId().getId().equals("artefacts")) {
                        ArrayList<Node> artefacts = items.getNodes(true);
                        artefacts.add(inv);
                    }
                }
                inventory.remove(inv);
                return true;
            }
        }
        return false;
    }

    public boolean consumeInventoryItem(String itemId){
        for(Node inv:inventory){
            if(inv.getId().getId().equals(itemId)){
                inventory.remove(inv);
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Graph getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Graph currentLocation) {
        this.currentLocation = currentLocation;
    }
}
