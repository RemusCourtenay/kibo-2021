package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.R;

/**
 * Standard builder class for setting up RobotOrder objects. Exists so that specific robot order
 * implementations can remain package-private.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class RobotOrderBuilder {

    private final Context context;
    private final KiboRpcApi api;

    // Honestly could've all been done in the order constructors..

    private final Map<String, RobotOrderType> stringOrderTypeMap;
    private final Pattern moveOrderPattern;
    private final Pattern qrCodeScanResultPattern;

    // Character that splits orders
    private final String orderSplitCharacter;
    // Character that splits numbers within the brackets inside move orders
    private final String orderInnerSplitCharacter;

    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;
    private final double flashlightOriginalBrightnessForScan;
    // Max number of times the move command will loop
    private final int moveLoopMax;
    // Max number of times the scan AR Code command will loop
    private final int scanARCodeLoopMax;

    // Should get set by the method setPointADash at some point
    private Point pointADash;

    public RobotOrderBuilder(Context context, KiboRpcApi api) {
        this.context = context;
        this.api = api;
        this.stringOrderTypeMap = buildStringOrderTypeMapFromStringsFile();
        this.moveOrderPattern = Pattern.compile(context.getString(R.string.move_order_regex_pattern));
        this.qrCodeScanResultPattern = Pattern.compile(context.getString(R.string.qr_code_scan_result_pattern));
        this.orderSplitCharacter = context.getString(R.string.order_split_character);
        this.orderInnerSplitCharacter = context.getString(R.string.order_inner_split_character);
        this.qrCodeScanResultSplitCharacter = context.getString(R.string.qr_code_scan_result_split_character);
        this.qrCodeScanResultInnerSplitCharacter = context.getString(R.string.qr_code_scan_result_inner_split_character);
        this.flashlightOriginalBrightnessForScan = context.getResources().getInteger(R.integer.flashlight_original_brightness_percent_for_scan)/(double)100;
        this.moveLoopMax = context.getResources().getInteger(R.integer.max_movement_loop_attempts);
        this.scanARCodeLoopMax = context.getResources().getInteger(R.integer.max_scan_ar_code_loop_attempts);
    }

    /**
     * Public builder method that allows access from outside of package to instantiate RobotOrder
     * objects. Attempts to discern what type of order each input is and then wrap it in the
     * correct RobotOrder implementation.
     *
     * @param fullOrderString   the full string containing each movement order within the section
     * @return                  an array of robot orders
     */
    public List<RobotOrder> buildOrders(String fullOrderString) {
        ArrayList<RobotOrder> orders = new ArrayList<>();
        String[] orderTexts = fullOrderString.split(this.orderSplitCharacter);


        // Trying to figure out what type of order each chunk of the string is
        for (String orderText: orderTexts) {
            RobotOrderType type;

            // if .get() returns null then the type isn't in the string to order map
            if ((type = this.stringOrderTypeMap.get(orderText)) != null) {
                RobotOrder order;

                // Wish I could use a switch statement.. turns out i could have...
                if (type == RobotOrderType.START_MISSION_ORDER) {
                    order = buildStartMissionOrder();
                } else if (type == RobotOrderType.SCAN_AR_CODE_ORDER) {
                    order = buildScanARCodeOrder();
                } else if (type == RobotOrderType.FIRE_LASER_ORDER) {
                    order = buildFireLaserOrder();
                } else if (type == RobotOrderType.FINISH_MISSION_ORDER) {
                    order = buildFinishMissionOrder();
                } else if (type == RobotOrderType.MOVE_ORDER) {
                    throw new RobotOrderException("Move orders should be written using bracket format");
                } else if (type == RobotOrderType.APPROACH_FIRING_POSITION_ORDER) {
                    order = buildApproachFiringPositionOrder();
                } else {
                    throw new RobotOrderException("RobotOrderType: " + type.name() + " not implemented in buildOrders");
                }
                orders.add(order);

            // If it's not in the map then it's probably a move order, checking it fits the format so we don't get errors later
            } else if (fitsMoveOrderFormat(orderText)) {
                orders.add(buildMoveOrder(orderText));

            // Doesn't fit any of the mapped values and isn't a correctly formatted move order so throwing exception
            } else {
                throw new RobotOrderException("Order: " + orderText + " doesn't fit any format described by the strings.xml file");
            }
        }
        return orders;
    }


    public void setPointADash(float x, float y, float z) {
        this.pointADash = new Point(x, y, z);
    }


    /**
     * Constructors for each order type
     */
    private RobotMoveOrder buildMoveOrder(String orderText) {
        //Separates orderText into two strings, one for position arguments and one for quaternion arguments
        String posStr = orderText.substring(2, orderText.indexOf("]"));
        String quatStr = orderText.substring(orderText.indexOf("[", 2) + 1, orderText.length() - 2);

        //Creating arguments with helper functions
        Point point = buildPointFromString(posStr);
        Quaternion quat = buildQuaternionFromString(quatStr);

        return new RobotMoveOrder(api, this.moveLoopMax, point, quat);
    }

    private RobotApproachFiringPositionOrder buildApproachFiringPositionOrder() {
        if (this.pointADash != null) {
            return new RobotApproachFiringPositionOrder(api, this.moveLoopMax, this.pointADash);
        } else {
            throw new RobotOrderException("Attempting to build ApproachFiringPositionOrder but Point A' hasn't been set");
        }


    }

    private RobotStartMissionOrder buildStartMissionOrder() {
        return new RobotStartMissionOrder(api);
    }

    private RobotScanARCodeOrder buildScanARCodeOrder() {
        return new RobotScanARCodeOrder(
                this.api,
                this.scanARCodeLoopMax,
                this.qrCodeScanResultPattern,
                this.qrCodeScanResultSplitCharacter,
                this.qrCodeScanResultInnerSplitCharacter,
                this.flashlightOriginalBrightnessForScan
        );
    }

    private RobotFireLaserOrder buildFireLaserOrder() {
        return null; // TODO...
    }

    private RobotFinishMissionOrder buildFinishMissionOrder() {
        return new RobotFinishMissionOrder(api);
    }


    /**
     * Helper function for building the order string decoder. Maps strings from the strings.xml
     * file to orderTypes. Maps a value to moveOrderType but this value shouldn't be used, move
     * orders should be written using the bracket format instead.
     *
     * @return A map of strings to order types for decoding order strings
     */
    private Map<String, RobotOrderType> buildStringOrderTypeMapFromStringsFile() {
        HashMap<String, RobotOrderType> stringOrderTypeMap = new HashMap<>();

        for (RobotOrderType orderType: RobotOrderType.values()) {
            stringOrderTypeMap.put(
                    this.context.getString(orderType.getOrderKey()),
                    orderType
            );
        }

        return stringOrderTypeMap;
    }


    /**
     * Helper function that checks if possible move order strings fit the format defined in the
     * strings.xml file.
     *
     * @param orderText An order chunk that is possibly a move order
     * @return          whether or not the chunk was a valid move order
     */
    private boolean fitsMoveOrderFormat(String orderText) {
        Matcher moveOrderMatcher = this.moveOrderPattern.matcher(orderText);
        return moveOrderMatcher.matches();
    }


    /** Helper methods for building kibo api objects from string arrays. Would've been way easier
     * if I could've used streams but the IDE said that wasn't available in this project's Java
     * version
     */
    private Point buildPointFromString(String posStr) {
        int numArgs = 3;
        String[] posStrings = posStr.split(this.orderInnerSplitCharacter);
        double[] posDoubles = new double[numArgs];

        for (int i = 0; i < numArgs; i++) {
            posDoubles[i] = Double.parseDouble(posStrings[i]);
        }

        return new Point(posDoubles[0], posDoubles[1], posDoubles[2]);
    }

    private Quaternion buildQuaternionFromString(String quatString) {
       int numArgs = 4;
       String[] posStrings = quatString.split(this.orderInnerSplitCharacter);
       float[] posFloats = new float[numArgs];

       for (int i = 0; i < numArgs; i++) {
           posFloats[i] = Float.parseFloat(posStrings[i]);
       }

       return new Quaternion(posFloats[0], posFloats[1], posFloats[2], posFloats[3]);
    }


}
