package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;

public class RobotOrderResult {

    private final Result result;
    private final int returnValue;

    RobotOrderResult(Result result) {
        this.result = result;
        this.returnValue = 0;
    }

    RobotOrderResult(Result result, int returnValue) {
        this.result = result;
        this.returnValue = returnValue;
    }

    public Result getResult() {
        return result;
    }

    public int getReturnValue() {
        return returnValue;
    }
}
