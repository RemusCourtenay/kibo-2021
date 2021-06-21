package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ARTagCollection {

    private final Mat tagIDsMat;
    private final List<Mat> tagCornersMat;
    private final List<ARTag> arTags;

    public ARTagCollection(Mat tagIDsMat, List<Mat> tagCornersMat) {
        this.tagIDsMat = tagIDsMat;
        this.tagCornersMat = tagCornersMat;
        this.arTags = buildListFromMats(tagIDsMat, tagCornersMat);
    }

    public Mat getTagIDsMat() {
        return this.tagIDsMat;
    }

    public List<Mat> getTagCornersMat() {
        return this.tagCornersMat;
    }

    public List<ARTag> getARTags() {
        return this.arTags;
    }

    public int getNumTags() {
        return this.arTags.size();
    }

    private static List<ARTag> buildListFromMats(Mat tagIDsMat, List<Mat> tagCornersMat) {
        List<ARTag> arTags = new ArrayList<>();

        for (int i = 0; i < tagIDsMat.size().width; i++) { // TODO... Check both mats are same width
            arTags.add(new ARTag(
                    /*
                    We assume that the IDs mat is at most a 4x1 (x,y) vector with each position containing a valid integer.
                    So we iterate over the first row (index 0), take the double[] output from each position,
                    get the first (and hopefully only) value from that array and cast it to an int.
                    */
                    (int)(tagIDsMat.get(0, i)[0]),
                    /*
                    We assume that the Corners mat is at most a 4x4 matrix with each column representing the four corners
                    of a specific tag. So we simply pass each column in to the constructor.
                    TODO... check that each column is length 4
                    */
                    tagCornersMat.get(i))
            );
        }
        return arTags;
    }

}
