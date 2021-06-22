package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotARTagReadOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class ARTagReaderWrapper {

    private static final String FOUND_LESS_THAN_FOUR_TAGS_MESSAGE = "Found less than four AR tags in image";
    private static final String SUCCESS_MESSAGE = "Successfully attempted to read image for AR tags and board";
    private static final int markerDictionaryID = Aruco.DICT_5X5_250;

    private static final double TAG_WIDTH = 5; // cm
    private static final double TAG_HEIGHT = 5; // cm


    public ARTagReaderWrapper(Context context) { // TODO... get static values from xml files via context

    }

    /**
     * Attempts to find the AR tags present in an image and if possible create a Board object from
     * them. Returns a Result value detailing the level of success that occurred.
     * The image argument should be 'cleaned' prior to calling this method as finding tags is a
     * slow process.
     *
     * @param cleanMatImage : An image that has been cropped/scaled to a relevant size
     *
     * @return : RobotOrderResult detailing the success of the operation and possibly containing
     *           bundled objects
     */
    public RobotOrderResult attemptReadImageForARTags(Mat cleanMatImage) {
        Board board;

        ARTagCollection arTagCollection = getTagsFromCleanImage(cleanMatImage);

        // Found tags but not enough,returning the incomplete list of tags and a result detailing the partial success
        if (arTagCollection.getNumTags() < 4) {
            return new RobotARTagReadOrderResult(true, 1, FOUND_LESS_THAN_FOUR_TAGS_MESSAGE, arTagCollection, null);

        // Successfully found both tags and board, returning positive result with bundled tags + board
        } else {
            board = getPredefinedBoard();
            return new RobotARTagReadOrderResult(true, 0, SUCCESS_MESSAGE, arTagCollection, board);
        }
    }


    /**
     * attempts to use the inbuilt Aruco detectMarkers() function to locate the location of each
     * AR tag in the inputted image and returns them in a more functional ARTag object format.
     *
     * @param cleanMatImage : image which contains one to four AR tags
     */
    private ARTagCollection getTagsFromCleanImage(Mat cleanMatImage) { // TODO...
        Mat ids = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        List<Mat> corners = new ArrayList<>();
        Aruco.detectMarkers(cleanMatImage, dictionary, corners, ids);
        return new ARTagCollection(ids, corners);
    }



    private Board getPredefinedBoard() {

        Mat matTagIDs = Converters.vector_int_to_Mat(Arrays.asList(4,1,2,3)); // Tag ids starting from top left

        /*
        // TODO... Figure out what coordinate system we're supposed to be using
        The kibo robot uses a coordinate system that's totally messed up.

        The 0,0,0 point is the top left.
        The left-right axis is y
        the up-down axis is z
        the forward-back axis is x


        this is me trying to calculate the points of the tags using this cursed system
         */

        List<MatOfPoint3f> listOfSpecificTagsCorners = Arrays.asList(
                getTagCornerPoints(0,0,0), // top left tag (id = 4)
                getTagCornerPoints(0,20,0), // top right tag (id = 1)
                getTagCornerPoints(0,20,10), // bottom right tag (id = 2)
                getTagCornerPoints(0,0,10) // bottom left tag (id = 3)
        );

        // Not entirely sure about this ngl
        List<Mat> listOfMatOfSpecificTagsCorners = new ArrayList<>();
        Converters.vector_vector_Point3f_to_Mat(listOfSpecificTagsCorners, listOfMatOfSpecificTagsCorners);

        return Board.create(listOfMatOfSpecificTagsCorners, Aruco.getPredefinedDictionary(markerDictionaryID), matTagIDs);
    }


    private MatOfPoint3f getTagCornerPoints(double xTopLeft, double yTopLeft, double zTopLeft) {
        Point3 topLeftPoint = new Point3(xTopLeft, yTopLeft, zTopLeft);
        Point3 topRightPoint = new Point3(xTopLeft, yTopLeft + TAG_WIDTH, zTopLeft);
        Point3 bottomRightPoint = new Point3(xTopLeft, yTopLeft + TAG_WIDTH, zTopLeft + TAG_HEIGHT);
        Point3 bottomLeftPoint = new Point3(xTopLeft, yTopLeft, zTopLeft + TAG_HEIGHT);

        List<Point3> cornerPointsList = Arrays.asList(topLeftPoint, topRightPoint, bottomRightPoint, bottomLeftPoint);

        return (MatOfPoint3f) Converters.vector_Point3f_to_Mat(cornerPointsList);

    }
}
