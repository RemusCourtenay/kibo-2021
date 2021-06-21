package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

/**
 * Abstract class that provides functionality for any atomic set of instructions the robot needs to
 * follow. Also provides an interface for Order types so the specifics of order type implementations
 * can be kept private from the other sections.
 * Extends KiboRpcService so that it's implementations can use the API commands.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */

public abstract class RobotOrder {

    final KiboRpcApi api;

    RobotOrder(KiboRpcApi api) {
        this.api = api;
    }

    /**
     * abstract method for implementations to Override.
     *
     * @return a RobotOrderResult object stating whether or not the order succeeded.
     */
    public abstract RobotOrderResult attemptOrder();

    public abstract String printOrderInfo();
}
