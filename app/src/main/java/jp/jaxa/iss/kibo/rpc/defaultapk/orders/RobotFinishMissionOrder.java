package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;

public class RobotFinishMissionOrder extends RobotOrder {

    @Override
    protected Result attemptOrderImplementation() {
        if (!api.reportMissionCompletion()) {
            throw new RuntimeException("Failed to report mission completion");
        }
        return null; // Kinda cursed, unfortunately startMission returns a bool not a Result
    }
}
