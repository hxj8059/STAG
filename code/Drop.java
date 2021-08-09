import java.util.*;
import STAGExceptions.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

public class Drop extends Command{

    String command;
    String[] splitCommand;

    public Drop(Player currentPlayer, ArrayList<Graph> entities, String command){
        this.currentPlayer = currentPlayer;
        this.entities = entities;
        this.command = command;
        splitCommand = command.split(" ");
    }

    public String parseAndGetString(){
        for(String commandWord:splitCommand){
            //if inventory contains the item, drop it to the location
            if(currentPlayer.isInventoryExist(commandWord)){
                currentPlayer.dropInventoryItem(commandWord);
                return "Drop "+commandWord;
            }
        }
        return "Error: Fail to drop";
    }

}
