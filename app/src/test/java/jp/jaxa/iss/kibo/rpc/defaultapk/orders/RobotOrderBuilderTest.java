package jp.jaxa.iss.kibo.rpc.defaultapk.orders;



import android.content.Context;
import android.test.ActivityTestCase;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;
import jp.jaxa.iss.kibo.rpc.defaultapk.TestResources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(MockitoJUnitRunner.class)
public class RobotOrderBuilderTest extends ActivityTestCase {

    @ClassRule
    public static final TestResources mockResources = new TestResources();

    private RobotOrderBuilder orderBuilder;
    private List<RobotOrder> orders;

    private static final String FIRST_ORDER_STRING = "START_MISSION|{[11.25,-9.8,4.87][0,0,-0.707,0.707]}|SCAN_QR_CODE";
    private static final String OPTION_ONE_ORDER_STRING = "{[10.99,-9.8,4.79][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[11.21,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_TWO_ORDER_STRING = "APPROACH_FIRING_POSITION|FIRE_LASER|{[11.21,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_THREE_ORDER_STRING = "{[10.99,-9.8,4.79][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[10.99,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_FOUR_ORDER_STRING = "{[10.99,-9.8,4.79][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[10.99,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_FIVE_ORDER_STRING = "{[10.61,-9.8,4.79][0,0,-0.707,0.707]}|{[10.62,-9.8,5.45][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[10.51,-9.8,5.45][0,0,-0.707,0.707]}|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_SIX_ORDER_STRING = "{[10.61,-9.8,4.79][0,0,-0.707,0.707]}|{[10.62,-9.8,5.45][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[10.51,-9.8,4.79][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_SEVEN_ORDER_STRING = "{[10.62,-9.8,4.79][0,0,-0.707,0.707]}|{[10.62,-9.8,5.45][0,0,-0.707,0.707]}|{[11.21,-9.8,5.45][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[11.21,-9.8,5.45][0,0,-0.707,0.707]}|{[10.51,-9.8,5.45][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String OPTION_EIGHT_ORDER_STRING = "{[10.62,-9.8,4.79][0,0,-0.707,0.707]}|{[10.62,-9.8,5.45][0,0,-0.707,0.707]}|{[11.21,-9.8,5.45][0,0,-0.707,0.707]}|APPROACH_FIRING_POSITION|FIRE_LASER|{[11.21,-9.8,5.45][0,0,-0.707,0.707]}|{[10.51,-9.8,5.45][0,0,-0.707,0.707]}|{[10.51,-8.0,4.79][0,0,-0.707,0.707]}";
    private static final String FINAL_ORDER_STRING = "{[10.6,-8.0,4.5][0,0,-0.707,0.707]}|FINISH_MISSION";

    @Before
    public void before() {
        orderBuilder = new RobotOrderBuilder(mockResources.mockContext, mockResources.nullApi);
        orders = new ArrayList<>();
    }

    @Test
    public void testFirstOrderString() {
        try {
            orders = orderBuilder.buildOrders(FIRST_ORDER_STRING);
            assertEquals(3,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionOneOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_ONE_ORDER_STRING);
            assertEquals(6,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionTwoOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_TWO_ORDER_STRING);
            assertEquals(5,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionThreeOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_THREE_ORDER_STRING);
            assertEquals(6,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionFourOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_FOUR_ORDER_STRING);
            assertEquals(6,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionFiveOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_FIVE_ORDER_STRING);
            assertEquals(7,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionSixOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_SIX_ORDER_STRING);
            assertEquals(6,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionSevenOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_SEVEN_ORDER_STRING);
            assertEquals(8,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOptionEightOrderString() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders(OPTION_EIGHT_ORDER_STRING);
            assertEquals(8,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }




    @Test
    public void testFinalOrderString() {
        try {
            orders = orderBuilder.buildOrders(FINAL_ORDER_STRING);
            assertEquals(2,orders.size());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAllZeroes() {
        try {
            orders = orderBuilder.buildOrders("{[0,0,0][0,0,0,0]}");
            assertEquals(
                    "Move order:\n" +
                    "Target point: [0.0][0.0][0.0]\n" +
                    "Target quaternion: [0.0][0.0][0.0][0.0]",
                    orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMissingNumbers() {
        try {
            orders = orderBuilder.buildOrders("{[,,,][,,,,]}");
            fail("Order builder didn't throw exception when given move order string missing values");
        } catch (RobotOrderException e) {
            assertEquals("Order builder correctly threw exception but error message didn't match what was expected",
                    "Order: {[,,,][,,,,]} doesn't fit any format described by the strings.xml file", e.getMessage());
        }
    }

    @Test
    public void testNegativeSigns() {
        try {
            orders = orderBuilder.buildOrders("{[0,-1,0][-1,0,-2,0]}");
            assertEquals("",
                    "Move order:\n" +
                            "Target point: [0.0][-1.0][0.0]\n" +
                            "Target quaternion: [-1.0][0.0][-2.0][0.0]",
                    orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPositiveSigns() {
        try {
            orders = orderBuilder.buildOrders("{[0,+1,0][+1,0,+2,0]}");
            assertEquals("",
                    "Move order:\n" +
                            "Target point: [0.0][1.0][0.0]\n" +
                            "Target quaternion: [1.0][0.0][2.0][0.0]",
                    orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testApproachFiringPositionBeforeSettingPointADash() {
        try {
            orders = orderBuilder.buildOrders("APPROACH_FIRING_POSITION");
            fail();
        } catch (RobotOrderException e) {
            assertEquals("","Attempting to build ApproachFiringPositionOrder but Point A' hasn't been set",e.getMessage());
        }
    }

    @Test
    public void testApproachFiringPositionAfterSettingPointADash() {
        orderBuilder.setPointADash(0,0,0);
        try {
            orders = orderBuilder.buildOrders("APPROACH_FIRING_POSITION");
            assertEquals("", "Approach firing position order:\n" +
                    "Target point: [0.0][0.0][0.0]\n" +
                    "Target quaternion: [0.0][0.0][-0.707][0.707]", orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testScanQRCodeOrder() {
        try {
            orders = orderBuilder.buildOrders("SCAN_QR_CODE");
            assertEquals("","Scan QR code order:",orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testStartMissionOrder() {
        try {
            orders = orderBuilder.buildOrders("START_MISSION");
            assertEquals("","Start mission order:",orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFinishMissionOrder() {
        try {
            orders = orderBuilder.buildOrders("FINISH_MISSION");
            assertEquals("","Finish mission order:",orders.get(0).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMultipleOrders() {
        try {
            orders = orderBuilder.buildOrders("START_MISSION|{[0,0,0][0,0,0,0]}|FINISH_MISSION");
            assertEquals(
                    "Returned list of orders is of wrong size.\n" +
                            "Expected: 3\n" +
                            "Returned: " + orders.size(), 3, orders.size());
            assertEquals("", "Start mission order:", orders.get(0).printOrderInfo());
            assertEquals("",
                    "Move order:\n" +
                            "Target point: [0.0][0.0][0.0]\n" +
                            "Target quaternion: [0.0][0.0][0.0][0.0]",
                    orders.get(1).printOrderInfo());
            assertEquals("", "Finish mission order:", orders.get(2).printOrderInfo());
        } catch (RobotOrderException e) {
            fail(e.getMessage());
        }
    }


}
