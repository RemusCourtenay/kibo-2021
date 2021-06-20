package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

class RobotFireLaserOrder extends RobotOrder {


    private static final int markerDictionaryID = Aruco.DICT_5X5_250;

    private final ImageHelper imageHelper;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper) {
        super(api);
        this.imageHelper = imageHelper;
    }

    private void attemptToAlignLaser(double poseAngle, double distance) {

    }

    @Override
    protected Result attemptOrderImplementation() {
        Mat image =  imageHelper.undistort(api.getMatNavCam(),api.getNavCamIntrinsics());
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat();//(4, 1, 0);
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);
        Board board = Board.create(corners, Aruco.getPredefinedDictionary(markerDictionaryID), ids);
        /**
         * Attempt with estimate pose board
         */
        // Mat counter = new Mat(4, 1, 0);
        // Size imageSize = new Size();
        Mat distCoeffs = new Mat();//4, 1, 0);
        Mat rvecs = new Mat();
        Mat tvecs = new Mat();
        // double vecs = Aruco.calibrateCameraAruco(corners, ids, counter, board, imageSize, image, distCoeffs);
        int poses = Aruco.estimatePoseBoard(corners, ids, board, image, distCoeffs, rvecs, tvecs);
        //Mat camMat = cameraMatrix(fc, new Size(frameSize.width / 2, frameSize.height / 2));
        /**
         * Attempt with calib3d solvePnP
         */
        Mat camMat = image;
        MatOfDouble coeff = new MatOfDouble(); // dummy

        MatOfPoint2f centers = new MatOfPoint2f();
        //Size patternSize = new Size(4, 11);
        //MatOfPoint3f grid = asymmetricalCircleGrid(patternSize);
        MatOfPoint3f grid = new MatOfPoint3f(image);
        Mat rvec = new MatOfFloat();
        Mat tvec = new MatOfFloat();

        MatOfPoint2f reprojCenters = new MatOfPoint2f();
        Calib3d.solvePnP(grid, centers, camMat, coeff, rvec, tvec, false);
        //Calib3d.solvePnP();
        return null; // TODO...
    }

    @Override
    public String printOrderInfo() {
        return null;
    }

    private void takeTenSnapShots() {
        for (int i = 0; i< 10; i++) {
            api.takeSnapshot();
            try {
                wait(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * getBoard makes and returns a board.
     * @return board
     */
    private Board getBoard() {
        Mat image =  imageHelper.undistort(api.getMatNavCam(),api.getNavCamIntrinsics());
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat();//(4, 1, 0);
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);
        while  ((ids.cols() < 2) && (ids.rows() < 2)) {
            adjustImage(ids);
            Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);

        }
        Board board = Board.create(corners, Aruco.getPredefinedDictionary(markerDictionaryID), ids);
        return board;
    }

    /**
     * adjustImage adjusts the image
     * @param ids
     */
    private void adjustImage(Mat ids) {
        /*int id1, id2, id3, id4;
        id1 = 0;
        id2 = 0;
        id3 = 0;
        id4 = 0;

        if (ids.rows()== 0) {
            if (ids.cols() == 0) {

            }
            if (ids.cols() == 1) {

            }
            if (ids.cols() == 2) {

            }
        }
         else if (ids.rows()== 1) {

            if (ids.cols() == 0) {

            }
            if (ids.cols() == 1) {

            }
            if (ids.cols() == 2) {

            }
        }
        else if (ids.rows()== 2) {

            if (ids.cols() == 0) {

            }
            if (ids.cols() == 1) {

            }
            if (ids.cols() == 2) {

            }
        }
        if (ids.rows()==0) {

        }
         else if (ids.rows()==1) {
            if (ids.cols() == 0) {

            }
             else if (ids.cols() == 1) {

            }
            else if (ids.cols() == 2) {

            }
            else if (ids.cols() == 3) {

            }
            else if (ids.cols() == 4) {

            }
        }*/
        List<Integer> id = new ArrayList<Integer>();
        for (int i = 0; i < ids.cols(); i++) {
            id.add(((int)(ids.get(1,i))[0]));
        }
        if (id.size() == 4) {
            return;
        } else if (id.size() == 3) {
            if (id.contains(1)) {
                if (id.contains(2)) {
                    if (id.contains(3)) {

                    } else if (id.contains(4)) {

                    }
                } else if (id.contains(3)) {
                    if (id.contains(2)) {

                    } else if (id.contains(4)) {

                    }
                } else if (id.contains(4)) {
                    if (id.contains(3)) {

                    } else if (id.contains(2)) {

                    }
                }
            } else if (id.contains(2)) {
                if (id.contains(1)) {
                    if (id.contains(3)) {

                    } else if (id.contains(4)) {

                    }
                } else if (id.contains(3)) {
                    if (id.contains(2)) {

                    } else if (id.contains(4)) {

                    }
                } else if (id.contains(4)) {
                    if (id.contains(3)) {

                    } else if (id.contains(4)) {

                    }
                }
            } else if (id.contains(3)) {
                if (id.contains(2)) {

                } else if (id.contains(1)) {

                } else if (id.contains(4)) {

                }
            } else if (id.contains(4)) {
                if (id.contains(2)) {

                } else if (id.contains(3)) {

                } else if (id.contains(1)) {

                }
            }
        } else if (id.size() == 2) {
            if (id.contains(1)) {
                if (id.contains(2)) {

                } else if (id.contains(3)) {

                } else if (id.contains(4)) {

                }
            } else if (id.contains(2)) {
                if (id.contains(1)) {

                } else if (id.contains(3)) {

                } else if (id.contains(4)) {

                }
            } else if (id.contains(3)) {
                if (id.contains(1)) {

                } else if (id.contains(2)) {

                } else if (id.contains(4)) {

                }
            } else if (id.contains(4)) {
                if (id.contains(1)) {

                } else if (id.contains(2)) {

                } else if (id.contains(3)) {

                }
            }
        } else if (id.size() == 1) {
            if (id.contains(1)) {

            } else if (id.contains(2)) {

            } else if (id.contains(3)) {

            } else if (id.contains(4)) {

            }
        }
        /*ids.get(0,0);
        ids.get(0,1);
        ids.get(1,0);
        ids.get(1,1);
        if (ids.rows()<0) {
            switch (ids.cols()) {
                case 0:

                    break;


            }
        }
        switch (ids) {
            case (ids.cols()<2):

        }*/
    }


}
