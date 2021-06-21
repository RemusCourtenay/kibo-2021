package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import jp.jaxa.iss.kibo.rpc.defaultapk.TestResources;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QRCodeReaderWrapperTest {

    private static final String QR_CODE_RETURN_TEXT = "sample text";
    private static final int EXPECTED_NUMBER_FLASHLIGHT_CHANGES_FOR_NULL = 4;
    private static final int EXPECTED_NUMBER_FLASHLIGHT_CHANGES_FOR_VALID = 2;

    @ClassRule
    public static TestResources testResources = new TestResources();

    @Mock
    ImageHelper mockImageHelper;
    @Mock
    QRCodeReader mockQRCodeReader;
    @Mock
    Result mockResult;

    private QRCodeReaderWrapper readerWrapper;


    @Before
    public void before() throws Exception{
        mockImageHelper = mock(ImageHelper.class);
        mockQRCodeReader = mock(QRCodeReader.class);
        mockResult = mock(Result.class);

        when(mockImageHelper.getBinaryBitmapFromMatImage(null, null)).thenReturn(null);
        when(mockQRCodeReader.decode(any())).thenReturn(mockResult);

        readerWrapper = new QRCodeReaderWrapper(testResources.nullApi, mockImageHelper, mockQRCodeReader, testResources.mockContext);

        Mockito.reset(testResources.nullApi);
    }

    @Test
    public void returnNullAllThreeTimesTest() {
        when(mockResult.getText()).thenReturn(null);

        try {
            readerWrapper.readQR();
        } catch (RobotOrderException e) {
            ArgumentCaptor<Float> acFloat  = ArgumentCaptor.forClass(Float.class);
            verify(testResources.nullApi, times(EXPECTED_NUMBER_FLASHLIGHT_CHANGES_FOR_NULL)).flashlightControlFront(acFloat.capture());

            assertArrayEquals(new Float[]{0.5f,0.7f,0.9f,0f}, acFloat.getAllValues().toArray());
        }
    }

    @Test
    public void returnSuccessfulResult() {
        when(mockResult.getText()).thenReturn(QR_CODE_RETURN_TEXT);

        try {
            readerWrapper.readQR();
        } catch (RobotOrderException e) {
            fail();
        }
        ArgumentCaptor<Float> acFloat = ArgumentCaptor.forClass(Float.class);
        verify(testResources.nullApi, times(EXPECTED_NUMBER_FLASHLIGHT_CHANGES_FOR_VALID)).flashlightControlFront(acFloat.capture());
        assertArrayEquals(new Float[]{0.5f, 0f}, acFloat.getAllValues().toArray());
    }



}
