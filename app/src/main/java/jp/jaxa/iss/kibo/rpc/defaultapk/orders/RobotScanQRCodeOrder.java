package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.qr.QRCodeDecoder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.qr.QRCodeReaderWrapper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

class RobotScanQRCodeOrder extends RobotOrder { // TODO... Comment

    private final QRCodeReaderWrapper qrCodeReaderWrapper;
    private final QRCodeDecoder qrCodeDecoder;


    RobotScanQRCodeOrder(KiboRpcApi api, QRCodeReaderWrapper qrCodeReaderWrapper, QRCodeDecoder qrCodeDecoder) {
        super(api);

        this.qrCodeReaderWrapper = qrCodeReaderWrapper;
        this.qrCodeDecoder = qrCodeDecoder;
    }

    @Override
    public RobotOrderResult attemptOrder() {
        String scanResultString = qrCodeReaderWrapper.readQR(); // Get result from other teams code here
        RobotOrderResult decodeResult = qrCodeDecoder.decodeQRCodeString(scanResultString);

        if (decodeResult.hasSucceeded()) {
            api.sendDiscoveredQR(scanResultString);
        }
        return decodeResult;
    }


    @Override
    public String printOrderInfo() {
        return "Scan QR code order:";
    } // TODO...
}
