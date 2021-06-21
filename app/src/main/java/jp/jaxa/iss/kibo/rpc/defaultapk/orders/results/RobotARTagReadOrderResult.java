package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import org.opencv.aruco.Board;

import java.util.List;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.ARTag;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.ARTagCollection;

public class RobotARTagReadOrderResult extends RobotOrderResult {

    private final ARTagCollection tags;
    private final Board board;

    public RobotARTagReadOrderResult(Result result, ARTagCollection tags, Board board) {
        super(result);
        this.tags = tags;
        this.board = board;
    }

    public RobotARTagReadOrderResult(boolean succeeded, int returnValue, String message, ARTagCollection tags, Board board) {
        super(succeeded, returnValue, message);
        this.tags = tags;
        this.board = board;
    }

    public ARTagCollection getARTagCollection() {
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
