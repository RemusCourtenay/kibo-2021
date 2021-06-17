package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;

public class RobotOrderResult {

    private final Result result;
    private final double[] returnValue;

    RobotOrderResult(Result result) {
        this.result = result;
        this.returnValue = null;
    }

    RobotOrderResult(Result result, double[] returnValue) {
        this.result = result;
        this.returnValue = returnValue;
    }

    public Result getResult() {
        return result;
    }

    public double[] getReturnValue() {
        return returnValue;
    } // TODO... throw error if null
}
