package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import gov.nasa.arc.astrobee.Result;

public class RobotScanQRCodeOrderResult extends RobotOrderResult {

    private final double[] qrCodeResults;

    public RobotScanQRCodeOrderResult(Result result, double[] qrCodeResults) {
        super(result);
        this.qrCodeResults = qrCodeResults;
    }

    public RobotScanQRCodeOrderResult(boolean succeeded, int returnValue, String message, double[] qrCodeResults) {
        super(succeeded, returnValue, message);
        this.qrCodeResults = qrCodeResults;
    }

    public double[] getQRCodeScanResults() {
        return this.qrCodeResults;
    }

    @Override
    public Class<? extends RobotOrderResult> getType() {
        return RobotScanQRCodeOrderResult.class;
    }
}
