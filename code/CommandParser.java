
import java.util.*;

import STAGExceptions.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class CommandParser {
    String command;
    PlayerList playerList;
    ArrayList<Graph> entities;
    JSONObject actions;
    String stringToPrint;

    public CommandParser(String command, PlayerList playerList, ArrayList<Graph> entities, JSONObject actions){
        this.command = command;
        this.playerList = playerList;
        this.entities = entities;
        this.actions = actions;
    }

    public void parse() throws NotExistException {
        //get the name of the player
        String name = command.split(":")[0];
        List<String> commands = Arrays.asList(command.toLowerCase().split(" "));

        //if the player is a new player
        if(!playerList.isPlayerExists(name)){
            playerList.addPlayerByName(name, entities);
        }
        //get current player
        Player currentPlayer = playerList.getPlayerByName(name);

        //if the command contains the "build-in" gameplay commands
        //inventory command
        if(commands.contains("inv")||commands.contains("inventory")){
            Inventory inventory = new Inventory(currentPlayer, entities);
            stringToPrint = inventory.parseAndGetString();
        }
        //get command
        else if(commands.contains("get")){
            Get get = new Get(currentPlayer, entities, command);
            stringToPrint = get.parseAndGetString();
        }
        //drop command
        else if(commands.contains("drop")){
            Drop drop = new Drop(currentPlayer, entities, command);
            stringToPrint = drop.parseAndGetString();
        }
        //goto command
        else if(commands.contains("goto")){
            Goto newGoto = new Goto(currentPlayer, entities, command);
            try{
                stringToPrint = newGoto.parseAndGetString();
            }catch (NotExistException nee){
                stringToPrint = nee.toString();
            }
        }
        //look command
        else if(commands.contains("look")){
            Look look = new Look(currentPlayer, entities);
            stringToPrint = look.parseAndGetString();
        }
        //health command
        else if(commands.contains("health")){
            stringToPrint = "Your current health is ";
            stringToPrint += String.valueOf(currentPlayer.getHealth());
        }
        //if the command contains other specified gameplay commands
        else{
            Action action = new Action(currentPlayer, entities, actions, command);
            stringToPrint = action.parseAndGetString();
        }
    }

    public String toString(){
        return stringToPrint;
    }

}
