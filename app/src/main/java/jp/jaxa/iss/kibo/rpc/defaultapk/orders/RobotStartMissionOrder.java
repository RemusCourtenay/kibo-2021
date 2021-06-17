package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

class RobotStartMissionOrder extends RobotOrder {

    RobotStartMissionOrder(KiboRpcApi api) {
        super(api);
    }

    @Override
    protected Result attemptOrderImplementation() {
        if (!api.startMission()) {
            throw new RobotOrderException("startMission() command failed");
        }
        return null; // Kinda cursed, unfortunately startMission returns a bool not a Result
    }

    @Override
    public String printOrderInfo() {
        return "Start mission order:";
    }
}
