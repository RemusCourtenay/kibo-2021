package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.qr;

import android.content.Context;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.QRCodeReader;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.R;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderException;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ImageHelper;

class QRCodeReaderWrapper {


    private final KiboRpcApi api;
    private final ImageHelper imageHelper;
    private final int loopMax;
    private final double flashlightOriginalBrightnessForScan;
    private final double flashlightFinalBrightnessForScan;

    private final QRCodeReader qrCodeReader;

    QRCodeReaderWrapper(KiboRpcApi api, ImageHelper imageHelper, QRCodeReader qrCodeReader, Context context) {
        this.api = api;
        this.imageHelper = imageHelper;
        this.qrCodeReader = qrCodeReader;
        this.loopMax = context.getResources().getInteger(R.integer.max_scan_ar_code_loop_attempts);
        this.flashlightOriginalBrightnessForScan = context.getResources().getInteger(R.integer.flashlight_original_brightness_percent_for_scan);
        this.flashlightFinalBrightnessForScan = context.getResources().getInteger(R.integer.flashlight_final_brightness_percent_for_scan);
    }

    /**
     * readQR is used to read a QR code
     * @return String content of the QR code
     */
    String readQR() { // TODO... Comment
        String contents;

        for (int count = 0; count < loopMax; count++) {
            Log.d("Read QR Code Attempt Initiated: ","Attempt " + (count+1));
            // Gradually increasing brightness of flashlight
            api.flashlightControlFront(getCalculatedBrightness(loopMax, count));

            // Getting image from nav cam as bitmap
            BinaryBitmap bitmap = this.imageHelper.getBinaryBitmapFromMatImage(api.getMatNavCam(), api.getNavCamIntrinsics());

            // If read successful then return
            if ((contents = readQRCodeFromBitmap(bitmap)) != null) {
                api.flashlightControlFront((float)0); // Turning flashlight off?
                return contents;
            }
        }
        api.flashlightControlFront((float)0); // Turning flashlight off?
        throw new RobotOrderException("Unable to retrieve QR code");
    }

    private float getCalculatedBrightness(int loopMax, int count) {
        Log.d("Calculating brightness for flashlight:", "current count: " + count + " out of " + loopMax);
        double maxCount = (double)(loopMax-1);

        // Camera is expected to go from dark to light so here we find the positive difference between the two
        double totalChangeInBrightness = flashlightFinalBrightnessForScan - flashlightOriginalBrightnessForScan;
        // Originally apply none of the change (count = 0), then finally apply all of the change (count = maxCount)
        double percentOfChangeToApply =  count/maxCount;

        // Return a float value equal to the initial brightness plus some percentage of the change
        float percentBrightness = (float)(this.flashlightOriginalBrightnessForScan+(totalChangeInBrightness*percentOfChangeToApply));
        // Returning a value between 0 and 1
        return percentBrightness/100f;
    }

    private String readQRCodeFromBitmap(BinaryBitmap bitmap) {
        Log.d("Attempting to retrieve QR code from binary bitmap","");
        try {
            qrCodeReader.reset();
            return qrCodeReader.decode(bitmap).getText();
        } catch (NotFoundException | FormatException | ChecksumException e) { // TODO... Do something with error messages
            return null;
        }
    }


}
