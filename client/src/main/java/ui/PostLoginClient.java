package ui;



import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class PostLoginClient {
    public final String serverUrl;
    private final ServerFacade server;
    private final AuthData userAuthData;

    // for observe (just for now while no live updates)
    private Collection<GameData> gameList = new ArrayList<>();

    public PostLoginClient(String serverUrl, AuthData authData) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.userAuthData = authData;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String create(String...params) throws ResponseException {
        //checking to make sure a username, password, and email only were sent in
        if (params.length == 1) {
            String gameName = params[0];

            CreateGameResponse createGameResponse = server.createGame(new CreateGameData(gameName), getAuthData().authToken());
            gameList.add(new GameData(createGameResponse.gameID(), null, null, null, new ChessGame()));
            return String.format("Created game with name %s and ID %s. ", gameName, createGameResponse.gameID());
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String list() throws ResponseException {
        GameListData gameList = server.listGames(getAuthData().authToken());
        return printGameList(gameList);
    }

    public String join(String... params) throws ResponseException {
        //checking to make sure a username and password only were sent in
        if (params.length == 2) {
            String gameID = params[0];
            String color = params[1].toLowerCase();
            int intGameID = 0;


            // checking if use ractualy entererd white or black
            if (!color.equals("white") && !color.equals("black")) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
            }

            // convert color to white or black type
            ChessGame.TeamColor teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            // checking if the user actually entered a string that can be converted to an integer
            try {
                intGameID = Integer.parseInt(gameID);
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist. must be an integer");
            }

            // checking game existence
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist");
            }

            server.joinGame(new JoinRequestData(teamColor, intGameID), getAuthData().authToken());
            return String.format("Joined game %s as team %s. \n" + printGame(intGameID, gameList, color), gameID, color);
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> <WHITE|BLACK>");
    }

    public String observe(String... params) throws ResponseException {
        //checking only a gameID was passed in
        if (params.length == 1) {
            String gameID = params[0];
            int intGameID = 0;

            // checking if ID is actually an integer
            try {
                intGameID = Integer.parseInt(gameID);
            }

            catch (Exception e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected as an integer: <ID>" );
            }

            // if game exists check
            if (!gameExists(intGameID)) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game ID doesn't exist." );
            }

            return printGame(intGameID, gameList, "black");
        }

        // below, used to be 400 in place of ClientError, not sure but ResExcep maps 400 to ClientErrors
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        server.logout(getAuthData().authToken());
        return "You have successfully logged out \n";
    }


    public AuthData getAuthData(){
        return this.userAuthData;
    }

    public String help() {
        return """
                    logout - logs you out of the server
                    create <name> - create a game
                    list - get  list of current games
                    join <ID> <white|black> - join game with specified ID as specified color
                    observe <ID> - observe the game with specified ID
                    help - get list of commands
                   """;
    }

    private boolean gameExists(int ID) {
        for (GameData game: gameList) {
            if (game.gameID() == ID) {
                return true;
            }
        }
        return false;
    }

    public String printGameList(GameListData gameList){
        if (gameList.games().isEmpty()) {
            return "There are currently no games. \n";
        }

        StringBuilder list = new StringBuilder();
        list.append("Here are all the Games:\n");

        // loping through each game and adding a newline to the string builder
        for (GameData game: gameList.games()) {
            int numID = game.gameID();
            String name = game.gameName();
            String whiteName = (game.whiteUsername() != null) ? game.whiteUsername() : "empty";
            String blackName = (game.blackUsername() != null) ? game.blackUsername() : "empty";
            list.append(String.format("GameID: %s, Game name: %s, White username: %s, Black username: %s\n", numID, name, whiteName, blackName));
        }
        // to make output more pretty lol
        list.append("\n");

        this.gameList.clear();
        this.gameList.addAll(gameList.games());

        return list.toString();
    }

    public String printGame(int gameID, Collection<GameData> gameList, String perspective){

        Map<ChessPiece.PieceType, String> pieceToString = Map.ofEntries(
                Map.entry(ChessPiece.PieceType.KING, "Ki"),
                Map.entry(ChessPiece.PieceType.QUEEN, "Qu"),
                Map.entry(ChessPiece.PieceType.ROOK, "Ro"),
                Map.entry(ChessPiece.PieceType.BISHOP, "Bi"),
                Map.entry(ChessPiece.PieceType.KNIGHT, "Kn"),
                Map.entry(ChessPiece.PieceType.PAWN, "Pa")
        );

        Map<ChessGame.TeamColor, String> colorToString = Map.ofEntries(
                Map.entry(ChessGame.TeamColor.WHITE, "30"),
                Map.entry(ChessGame.TeamColor.BLACK, "20"));

        // getting chessgame's board
        ChessBoard board = null;
        for (GameData gameData: gameList) {
            if (gameData.gameID() == gameID) {
                board = gameData.game().getBoard();
            }
        }

        String vertEndRowsBlack = "\u001B[102;102;1m' '\u001B[0m" +
                "\u001B[30;102;1m a  \u001B[0m" +
                "\u001B[30;102;1m b  \u001B[0m" +
                "\u001B[30;102;1m c  \u001B[0m" +
                "\u001B[30;102;1m d  \u001B[0m" +
                "\u001B[30;102;1m e  \u001B[0m" +
                "\u001B[30;102;1m f  \u001B[0m" +
                "\u001B[30;102;1m g  \u001B[0m" +
                "\u001B[30;102;1m h  \u001B[0m" +
                "\u001B[102;102;1m' '\u001B[0m" +
                "\n";

        String vertEndRowsWhite = "\u001B[102;102;1m' '\u001B[0m" +
                "\u001B[30;102;1m h  \u001B[0m" +
                "\u001B[30;102;1m g  \u001B[0m" +
                "\u001B[30;102;1m f  \u001B[0m" +
                "\u001B[30;102;1m e  \u001B[0m" +
                "\u001B[30;102;1m d  \u001B[0m" +
                "\u001B[30;102;1m c  \u001B[0m" +
                "\u001B[30;102;1m b  \u001B[0m" +
                "\u001B[30;102;1m a  \u001B[0m" +
                "\u001B[102;102;1m' '\u001B[0m" +
                "\n";

        StringBuilder chessBoardString = new StringBuilder();

        // adding 1st row accordig to black or white perspective
        if (perspective.equals("white")) {
            chessBoardString.append(vertEndRowsBlack);
        }
        else {
            chessBoardString.append(vertEndRowsWhite);
        }

        for (int i=0; i<8; i++) {
            // reverse i if building other perspective
            int newI = (perspective.equals("white")) ? i : (7 - i);

            // building rows one by one
            StringBuilder rowString = new StringBuilder();
            String rowEndSquare = String.format("\u001B[30;102;1m %s \u001B[0m", Integer.toString(8 - newI));

            rowString.append(rowEndSquare);
            for (int j=0; j<8; j++) {

                // reverse j if building from other persepctive
                int newJ = (perspective.equals("white")) ? j : 8 - (j + 1);
                String backColor = ((newI + newJ) % 2 == 0) ? "102" : "42";
                if (board.squares[newI][newJ] != null) {
                    rowString.append(String.format("\u001B[%s;%s;1m %s \u001B[0m",
                            colorToString.get(board.squares[newI][newJ].getTeamColor()),
                            backColor,
                            pieceToString.get(board.squares[newI][newJ].getPieceType())));
                }
                else {
                    rowString.append(String.format("\u001B[%s;%s;1m %s \u001B[0m",
                            backColor,
                            backColor,
                            "  "));
                }
            }
            rowString.append(rowEndSquare).append("\n");

            chessBoardString.append(rowString);
        }

        if (perspective.equals("white")) {
            chessBoardString.append(vertEndRowsBlack);
        }
        else {
            chessBoardString.append(vertEndRowsWhite);
        }

        return chessBoardString.toString();

    }
}
