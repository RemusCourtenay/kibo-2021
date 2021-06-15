package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderType;


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


    private final List<RobotOrder> orders;
    // Index of currently active order
    private int currentOrder = 0;


    /**
     * Sole constructor. The orders parameter must contain a full string representation of the orders for
     * an entire expedition as the order array cannot be changed once instantiated. The expedition
     * will attempt each order in the string sequentially so ordering must be retained.
     *
     * @param fullOrderString    A full string containing all orders correctly formatted
     * @see   RobotOrder
     */
    public ExpeditionWrapper(Context context, String fullOrderString) {
        RobotOrderBuilder orderBuilder = new RobotOrderBuilder(context);
        this.orders = Collections.unmodifiableList(orderBuilder.buildOrders(fullOrderString));
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
