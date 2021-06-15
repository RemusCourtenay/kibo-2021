package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;

class RobotScanARCodeOrder extends RobotOrder {

    private int scanResult;

    @Override
    protected Result attemptOrderImplementation() {

        scanResult = 0; // Get result from other teams code here

        return null; // TODO...
    }

    @Override
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation(), this.scanResult);
    }

}
