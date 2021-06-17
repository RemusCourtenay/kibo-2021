package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;
import android.content.res.Resources;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RobotOrderBuilderTest {

    private static final String MOCK_PATTERN_RESOURCE = "(?:\\{\\[)(([+|-]?\\d+([\\.]\\d+)?)[,]){2}([+|-]?\\d+([\\.]\\d+)?){1}(?:\\]\\[)(([+|-]?\\d+([\\.]\\d+)?)[,]){3}([+|-]?\\d+([\\.]\\d+)?){1}(?:\\]\\})";
    private static final String MOCK_START_MISSION_KEY = "START_MISSION";
    private static final String MOCK_MOVE_ORDER_KEY = "Do not use this, use the bracket format instead";
    private static final String MOCK_SCAN_AR_CODE_KEY = "SCAN_AR_CODE";
    private static final String MOCK_FIRE_LASER_KEY = "FIRE_LASER";
    private static final String MOCK_FINISH_MISSION_ORDER_KEY = "FINISH_MISSION";

    private static final String MOCK_SPLIT_CHARACTER = "\\|";
    private static final String MOCK_INNER_SPLIT_CHARACTER = ",";

    private static final int MOCK_MAX_MOVE_ATTEMPTS = 3;

    @Mock
    Context mockContext;

    @Mock
    Resources mockResources;

    KiboRpcApi mockApi;

    private RobotOrderBuilder orderBuilder;
    private List<RobotOrder> orders;

    @Before
    public void initialize() {

        when(mockContext.getString(R.string.move_order_regex_pattern)).thenReturn(MOCK_PATTERN_RESOURCE);
        when(mockContext.getString(R.string.start_mission_order_key)).thenReturn(MOCK_START_MISSION_KEY);
        when(mockContext.getString(R.string.move_order_key)).thenReturn(MOCK_MOVE_ORDER_KEY);
        when(mockContext.getString(R.string.scan_ar_code_order_key)).thenReturn(MOCK_SCAN_AR_CODE_KEY);
        when(mockContext.getString(R.string.fire_laser_order_key)).thenReturn(MOCK_FIRE_LASER_KEY);
        when(mockContext.getString(R.string.finish_mission_order_key)).thenReturn(MOCK_FINISH_MISSION_ORDER_KEY);

        when(mockContext.getString(R.string.order_split_character)).thenReturn(MOCK_SPLIT_CHARACTER);
        when(mockContext.getString(R.string.order_inner_split_character)).thenReturn(MOCK_INNER_SPLIT_CHARACTER);

        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getInteger(R.integer.max_movement_loop_attempts)).thenReturn(MOCK_MAX_MOVE_ATTEMPTS);


        this.orderBuilder = new RobotOrderBuilder(mockContext, mockApi);
        this.orders = new ArrayList<>();
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
