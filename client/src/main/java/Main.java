import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // i think the above code is just example filler

        //store server url

        // while true
            // run pre login repl (make it return authdata if logged in or registered, and null if quit
            //store authdata retreved from the prelogin run
            // if auth == null then break loop and user quit so exit
            // init post login repl
            // run post repl; (and its repl loop breaks when user logouts)

        // if authData is not null, then start make postlogin repl and call its run
        // NOTE: post login repl should start the game repl loop when someone does "join"

        // outside while, print exit meessage
    }
}