package jp.jaxa.iss.kibo.rpc.defaultapk;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrder;


/**
 * Wrapper class to handle entire expedition. Exists in order to make testing order sets easier
 * for team members unfamiliar with coding.
 * Class will take significant time to return as it blocks while orders are being attempted.
 * Multithreading to allow for querying of expedition status whilst running is not supported by
 * Kibo robot hardware.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class ExpeditionWrapper {

    // Immutable list of orders
    private final List<RobotOrder> orders;
    // Index of currently active order
    private int currentOrder = 0;


    /**
     * Sole constructor. The orders parameter must contain a full array of orders representing an
     * entire expedition as the order array cannot be changed once instantiated. The expedition
     * will attempt each order in the array sequentially so ordering must be retained.
     *
     * @param orders    an array of orders for the robot to follow
     * @see             RobotOrder
     */
    public ExpeditionWrapper(RobotOrder[] orders) {
        this.orders = Collections.unmodifiableList(Arrays.asList(orders));
        // TODO...
    }

    /**
     * Starts the wrapper iterating through it's orders. Will cause it's caller to block for a
     * long period as multithreading of orders is not implemented.
     */
    public void startExpedition() {
        // TODO...
    }
}
