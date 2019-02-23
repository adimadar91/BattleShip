package Engine.BattleShipGame;

public class SimpleBoards
{
    private final Square[][] myBoard;
    private final Square[][] trackingBoard;

    public SimpleBoards(Square[][] myBoard, Square[][] trackingBoard){
        this.myBoard = myBoard;
        this.trackingBoard = trackingBoard;
    }
}
