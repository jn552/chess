package model;

import chess.ChessGame;

public record JoinRequestData(ChessGame.TeamColor playerColor, int gameID) {
}
