import java.util.ArrayList;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Inventory extends Command{

    public Inventory(Player currentPlayer, ArrayList<Graph> entities){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
    }

    public String parseAndGetString(){
        if(currentPlayer.inventory.size()==0) stringToPrint = "Empty";
        else stringToPrint = "You have: \n";
        for(Node inv:currentPlayer.inventory){
            stringToPrint += inv.getId().getId() +" : "+ inv.getAttribute("description")+ "\n";
        }
        return stringToPrint;
    }
}
