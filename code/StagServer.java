import java.io.*;
import java.net.*;
import java.util.*;
import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import org.json.simple.*;
import org.json.simple.parser.*;

class StagServer
{
    PlayerList playerList = new PlayerList();
    ArrayList<Graph> entities;
    JSONObject actions;

    public static void main(String args[])
    {
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber)
    {
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            //initialise the entities and actions
            parseEntitiesAndActions(entityFilename, actionFilename);
            while(true) acceptNextConnection(ss);
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    private void acceptNextConnection(ServerSocket ss)
    {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException
    {
        String command = in.readLine();
        //parse command
        CommandParser commandParser = new CommandParser(command, playerList, entities, actions);
        try{
            commandParser.parse();
        }catch (Exception e){
            out.write("ERROR:\n ");
            e.printStackTrace();
        }
        out.write(commandParser.toString());
    }

    private void parseEntitiesAndActions(String entityFilename, String actionFilename) throws IOException, com.alexmerz.graphviz.ParseException, org.json.simple.parser.ParseException {
        try{
            FileReader entityReader = new FileReader(entityFilename);
            Parser entityParser = new Parser();
            entityParser.parse(entityReader);
            entities = entityParser.getGraphs();
            FileReader actionReader = new FileReader(actionFilename);
            JSONParser actionParser = new JSONParser();
            actions = (JSONObject) actionParser.parse(actionReader);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (com.alexmerz.graphviz.ParseException gpe) {
            System.out.println(gpe);
        } catch (org.json.simple.parser.ParseException jpe) {
            System.out.println(jpe);
        }
    }
}
