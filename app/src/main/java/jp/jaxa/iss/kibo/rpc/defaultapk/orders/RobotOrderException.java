package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

public class RobotOrderException extends RuntimeException {

    private final String errorMessage;

    public RobotOrderException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
