package websocket.commands;

import chess.ChessMove;

public class MakeMoveAction extends UserGameCommand {
    public ChessMove chessMove;

    public MakeMoveAction(String authToken, Integer gameID, ChessMove chessMove) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove() {
        return this.chessMove;
    }
}
