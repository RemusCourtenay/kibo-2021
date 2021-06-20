package jp.jaxa.iss.kibo.rpc.defaultapk.orders;



import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.TestResources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(MockitoJUnitRunner.class)
public class RobotOrderBuilderTest {

    @ClassRule
    public static final TestResources mockResources = new TestResources();

    private RobotOrderBuilder orderBuilder;
    private List<RobotOrder> orders;

    @Before
    public void before() {
        orderBuilder = new RobotOrderBuilder(mockResources.mockContext, mockResources.nullApi);
        orders = new ArrayList<>();
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
