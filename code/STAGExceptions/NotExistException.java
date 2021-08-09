package STAGExceptions;

public class NotExistException extends Exception{
    String name;
    public NotExistException(String name){
        this.name = name;
    }

    public String toString(){
        return this.getClass().getName() + "name= " + name;
    }
}
