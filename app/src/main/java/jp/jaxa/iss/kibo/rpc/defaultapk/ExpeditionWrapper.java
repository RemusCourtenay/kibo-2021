package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.util.Log;

import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderException;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;


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

    // Builder object for translating order strings into lists of RobotOrders
    private final RobotOrderBuilder orderBuilder;
    private int stageNum;
    /**
     * Sole constructor. Requires the Context of the Activity it's called from to access data
     * stored in the resources folders.
     */
    public ExpeditionWrapper(RobotOrderBuilder orderBuilder) {
        this.orderBuilder = orderBuilder;
        this.stageNum = 1;
    }

    /**
     * Starts the wrapper iterating through it's orders. Will cause it's caller to block for a
     * long period as multithreading of orders is not implemented.
     * The orders parameter must contain a full string representation of the orders for
     * an entire expedition stage as the order array cannot be changed once instantiated.
     * The expedition will attempt each order in the string sequentially so ordering must be
     * retained.
     *
     * @param fullOrderString a single string containing a full set of orders using correct formatting
     * @return the RobotOrderResult of the final command in the order string
     */
    public RobotOrderResult attemptExpeditionStage(String fullOrderString) { // Should probably throw a specific exception when an order fails rather than RuntimeException but whatever
        Log.d("Starting New Expedition Stage: ", "Stage " + stageNum);
        stageNum++;
        List<RobotOrder> orders = this.orderBuilder.buildOrders(fullOrderString);

        RobotOrderResult result = null;
        // Should probably have more going on here but whatever
        for (RobotOrder order: orders) {
            Log.d("Attempting Order: ", order.printOrderInfo());
            result = order.attemptOrder();
            if (!result.hasSucceeded()) {
                Log.d("\n---------PROGRAM CRASH ---------",
                        "\nResult message: " + result.getMessage() +
                                "\nResult return value: " + result.getReturnValue() +
                                "\nResult type: " + result.getType()
                );
                return null;
            }
        }
        return result;
    }
}
