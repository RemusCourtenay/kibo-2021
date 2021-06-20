package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Pattern;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;
import jp.jaxa.iss.kibo.rpc.defaultapk.TestResources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RobotScanQRCodeOrderTest{

    @ClassRule
    public static final TestResources testResources = new TestResources();

    @Mock
    private QRCodeReaderWrapper mockQRCodeReaderWrapper;

    private RobotScanQRCodeOrder scanQRCodeOrder;



}
