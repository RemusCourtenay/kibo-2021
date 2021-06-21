package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.content.Context;
import android.content.res.Resources;

import org.junit.rules.ExternalResource;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestResources extends ExternalResource {


    private static final String MOCK_START_MISSION_KEY = "START_MISSION";
    private static final String MOCK_MOVE_KEY = "Do not use this, use the bracket format instead";
    private static final String MOCK_APPROACH_FIRING_POSITION_KEY = "APPROACH_FIRING_POSITION";
    private static final String MOCK_SCAN_AR_CODE_KEY = "SCAN_QR_CODE";
    private static final String MOCK_FIRE_LASER_KEY = "FIRE_LASER";
    private static final String MOCK_FINISH_MISSION_ORDER_KEY = "FINISH_MISSION";


    private static final String MOCK_PATTERN_RESOURCE = "(?:\\{\\[)(([+|-]?\\d+([\\.]\\d+)?)[,]){2}([+|-]?\\d+([\\.]\\d+)?){1}(?:\\]\\[)(([+|-]?\\d+([\\.]\\d+)?)[,]){3}([+|-]?\\d+([\\.]\\d+)?){1}(?:\\]\\})";
    private static final String MOCK_SPLIT_CHARACTER = "\\|";
    private static final String MOCK_INNER_SPLIT_CHARACTER = ",";

    private static final String MOCK_QR_SCAN_RESULT_PATTERN = "\\{\"p\":[1-8],\"x\":[+|-]?\\d+(\\.\\d+)?,\"y\":[+|-]?\\d+(\\.\\d+)?,\"z\":[+|-]?\\d+(\\.\\d+)?\\}";
    private static final String MOCK_QR_SCAN_RESULT_SPLIT_CHARACTER = ",";
    private static final String MOCK_QR_SCAN_RESULT_INNER_SPLIT_CHARACTER = ":";

    private static final int MOCK_ORIGINAL_FLASHLIGHT_BRIGHTNESS = 50;
    private static final int MOCK_FINAL_FLASHLIGHT_BRIGHTNESS = 90;
    private static final int MOCK_MAX_MOVE_ATTEMPTS = 3;
    private static final int MOCK_MAX_SCAN_ATTEMPTS = 3;

    private static final int MOCK_KIBO_CAM_IMAGE_HEIGHT = 960;
    private static final int MOCK_KIBO_CAM_IMAGE_WIDTH = 1280;
    private static final int MOCK_PERCENT_REMOVED = 40;
    private static final int MOCK_RESIZE_IMAGE_HEIGHT = 384;
    private static final int MOCK_RESIZE_IMAGE_WIDTH = 288;


    // I feel my lecturers rolling in their graves
    public Context mockContext;

    public Resources mockResources;

    public KiboRpcApi nullApi;


    @Override
    public void before() {

        mockContext = mock(Context.class);
        mockResources = mock(Resources.class);
        nullApi = mock(KiboRpcApi.class);


        when(mockContext.getString(R.string.start_mission_order_key)).thenReturn(MOCK_START_MISSION_KEY);
        when(mockContext.getString(R.string.move_order_key)).thenReturn(MOCK_MOVE_KEY);
        when(mockContext.getString(R.string.approach_firing_position_order_key)).thenReturn(MOCK_APPROACH_FIRING_POSITION_KEY);
        when(mockContext.getString(R.string.scan_qr_code_order_key)).thenReturn(MOCK_SCAN_AR_CODE_KEY);
        when(mockContext.getString(R.string.fire_laser_order_key)).thenReturn(MOCK_FIRE_LASER_KEY);
        when(mockContext.getString(R.string.finish_mission_order_key)).thenReturn(MOCK_FINISH_MISSION_ORDER_KEY);

        when(mockContext.getString(R.string.move_order_regex_pattern)).thenReturn(MOCK_PATTERN_RESOURCE);
        when(mockContext.getString(R.string.order_split_character)).thenReturn(MOCK_SPLIT_CHARACTER);
        when(mockContext.getString(R.string.order_inner_split_character)).thenReturn(MOCK_INNER_SPLIT_CHARACTER);

        when(mockContext.getString(R.string.qr_code_scan_result_pattern)).thenReturn(MOCK_QR_SCAN_RESULT_PATTERN);
        when(mockContext.getString(R.string.qr_code_scan_result_split_character)).thenReturn(MOCK_QR_SCAN_RESULT_SPLIT_CHARACTER);
        when(mockContext.getString(R.string.qr_code_scan_result_inner_split_character)).thenReturn(MOCK_QR_SCAN_RESULT_INNER_SPLIT_CHARACTER);


        when(mockContext.getResources()).thenReturn(mockResources);

        when(mockResources.getInteger(R.integer.max_movement_loop_attempts)).thenReturn(MOCK_MAX_MOVE_ATTEMPTS);
        when(mockResources.getInteger(R.integer.max_scan_ar_code_loop_attempts)).thenReturn(MOCK_MAX_SCAN_ATTEMPTS);
        when(mockResources.getInteger(R.integer.flashlight_original_brightness_percent_for_scan)).thenReturn(MOCK_ORIGINAL_FLASHLIGHT_BRIGHTNESS);
        when(mockResources.getInteger(R.integer.flashlight_final_brightness_percent_for_scan)).thenReturn(MOCK_FINAL_FLASHLIGHT_BRIGHTNESS);

        when(mockResources.getInteger(R.integer.kibo_cam_image_height)).thenReturn(MOCK_KIBO_CAM_IMAGE_HEIGHT);
        when(mockResources.getInteger(R.integer.kibo_cam_image_width)).thenReturn(MOCK_KIBO_CAM_IMAGE_WIDTH);
        when(mockResources.getInteger(R.integer.percent_of_image_that_crop_removes)).thenReturn(MOCK_PERCENT_REMOVED);
        when(mockResources.getInteger(R.integer.resize_image_height)).thenReturn(MOCK_RESIZE_IMAGE_HEIGHT);
        when(mockResources.getInteger(R.integer.resize_image_width)).thenReturn(MOCK_RESIZE_IMAGE_WIDTH);


        when(nullApi.getMatNavCam()).thenReturn(null);
        when(nullApi.getNavCamIntrinsics()).thenReturn(null);
    }





    @Override
    public void after() {

    }

}
