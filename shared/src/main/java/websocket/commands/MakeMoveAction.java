package websocket.commands;

import chess.ChessMove;

public class MakeMoveAction extends UserGameCommand {

    public MakeMoveAction(String authToken, Integer gameID, ChessMove chessMove) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, chessMove);
    }
}
