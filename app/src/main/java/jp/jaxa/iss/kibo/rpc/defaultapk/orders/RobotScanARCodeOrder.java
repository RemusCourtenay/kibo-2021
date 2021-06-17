package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

class RobotScanARCodeOrder extends RobotOrder {

    private int scanResult;

    RobotScanARCodeOrder(KiboRpcApi api) {
        super(api);
    }

    @Override
    protected Result attemptOrderImplementation() {

        scanResult = 0; // Get result from other teams code here

        return null; // TODO...
    }

    @Override
    public String printOrderInfo() {
        return null;
    }

    /**
     * Overriding the standard attemptOrder() method to allow for the addition of the scan result
     */
    @Override
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation(), this.scanResult);
    }

}
