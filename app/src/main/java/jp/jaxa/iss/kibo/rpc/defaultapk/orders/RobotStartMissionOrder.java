package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

class RobotStartMissionOrder extends RobotOrder {

    private static final String START_ORDER_FAILED_MESSAGE = "Failed to start mission";
    private static final String START_ORDER_SUCCEEDED_MESSAGE = "Successfully started mission";

    RobotStartMissionOrder(KiboRpcApi api) {
        super(api);
    }

    @Override
    public RobotOrderResult attemptOrder() {
        if (!api.startMission()) {
            return new GenericRobotOrderResult(
                    false,
                    1,
                    START_ORDER_FAILED_MESSAGE
            );
        }
        return new GenericRobotOrderResult(
                true,
                0,
                START_ORDER_SUCCEEDED_MESSAGE
        );
    }

    @Override
    public String printOrderInfo() {
        return "Start mission order:";
    }
}
