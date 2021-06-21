package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import gov.nasa.arc.astrobee.Result;

public class GenericRobotOrderResult extends RobotOrderResult {

    public GenericRobotOrderResult(Result result) {
        super(result);
    }

    public GenericRobotOrderResult(boolean succeeded, int returnValue, String message) {
        super(succeeded, returnValue, message);
    }

    @Override
    public Class<? extends RobotOrderResult> getType() {
        return GenericRobotOrderResult.class;
    }
}
