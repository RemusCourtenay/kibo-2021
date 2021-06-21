package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import gov.nasa.arc.astrobee.Result;

public class RobotDecodeQRCodeResult extends RobotOrderResult {

    private final double[] decodedResults;

    public RobotDecodeQRCodeResult(Result result, double[] decodedResults) {
        super(result);
        this.decodedResults = decodedResults;
    }

    public RobotDecodeQRCodeResult(boolean succeeded, int returnValue, String message, double[] decodedResults) {
        super(succeeded, returnValue, message);
        this.decodedResults = decodedResults;
    }

    public double[] getDecodedResults() {
        return this.decodedResults;
    }


    @Override
    public Class<? extends RobotOrderResult> getType() {
        return RobotDecodeQRCodeResult.class;
    }
}

