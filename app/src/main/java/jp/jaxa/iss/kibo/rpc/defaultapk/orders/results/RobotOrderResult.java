package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import android.util.Log;

import gov.nasa.arc.astrobee.Result;

public abstract class RobotOrderResult {

    private final boolean succeeded;
    private final int returnValue;
    private final String message;

    public RobotOrderResult(Result result) {
        this.succeeded = result.hasSucceeded();
        this.returnValue = result.getStatus().getValue();
        this.message = result.getMessage();
        logSelf();
    }

    public RobotOrderResult(boolean succeeded, int returnValue, String message) {
        this.succeeded = succeeded;
        this.returnValue = returnValue;
        this.message = message;
        logSelf();
    }

    private void logSelf() {
        Log.d("RobotOrderResult:",
                "\nhasSucceeded: " + succeeded +
                "\nreturnValue: " + this.returnValue +
                "\nmessage: " + this.message
        );
    }

    public boolean hasSucceeded() {
        return succeeded;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public String getMessage() {
        return this.message;
    }

    public abstract Class<? extends RobotOrderResult> getType();
}
