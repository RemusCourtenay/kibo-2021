package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;

/**
 * Implementation of RobotOrder concerning the order for the Kibo Robot to announce that it has
 * completed it's mission by playing the audio clip 'astrobee.mp3' located in the res/raw/ folder
 * As the API's reportMissionCompletion method does not return a Result object, this class will
 * always return an empty RobotOrderResult.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class RobotFinishMissionOrder extends RobotOrder {

    /**
     * Implementation of the specific order. Attempts to run the API's reportMissionCompletion
     * method and throws an exception if the order fails.
     */
    @Override
    protected Result attemptOrderImplementation() {
        if (!api.reportMissionCompletion()) {
            throw new RobotOrderException("Failed to report mission completion");
        }
        return null; // Kinda cursed, unfortunately startMission returns a bool not a Result
    }

    @Override
    public String printOrderInfo() {
        return null;
    }
}
