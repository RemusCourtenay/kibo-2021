package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

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
     * Public method for starting the order, wraps the private implementation specific method to
     * allow for different implementations to return different types of data through the
     * RobotOrderResult object.
     *
     * @return a RobotOrderResult containing information produced by the order.
     */
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation());
    }

    /**
     * Protected abstract method for implementations to Override.
     *
     * @return a Result object stating whether or not the order succeeded.
     */
    protected abstract Result attemptOrderImplementation();

    public abstract String printOrderInfo();
}
