package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.util.Log;

public class RobotOrderException extends RuntimeException {

    private final String errorMessage;

    public RobotOrderException(String errorMessage) {
        this.errorMessage = errorMessage;
        Log.d("ROBOT ORDER EXCEPTION THROWN:", "Message: " + errorMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
