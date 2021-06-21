package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import org.opencv.aruco.Board;

import java.util.List;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.ARTag;

public class RobotARTagReadOrderResult extends RobotOrderResult {

    private final List<ARTag> tags;
    private final Board board;

    public RobotARTagReadOrderResult(Result result, List<ARTag> tags, Board board) {
        super(result);
        this.tags = tags;
        this.board = board;
    }

    public RobotARTagReadOrderResult(boolean succeeded, int returnValue, String message, List<ARTag> tags, Board board) {
        super(succeeded, returnValue, message);
        this.tags = tags;
        this.board = board;
    }

    public List<ARTag> getARTags() {
        return this.tags;
    }

    public Board getBoard() {
        return this.board;
    }

    @Override
    public Class<? extends RobotOrderResult> getType() {
        return RobotARTagReadOrderResult.class;
    }
}
