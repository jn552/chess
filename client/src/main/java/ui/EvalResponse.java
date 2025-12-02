package ui;

public class EvalResponse{
    public String message;
    public Integer gameID;
    public String color;

    public EvalResponse(String message, Integer gameID, String color){
        this.message = message;
        this.gameID = gameID;
        this.color = color;
    }

}

