package edu.gonzaga.opencvtest;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
//import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;

import android.database.sqlite.*;
import static android.database.sqlite.SQLiteDatabase.CREATE_IF_NECESSARY;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import static org.opencv.imgcodecs.Imgcodecs.imdecode;

public class CameraView extends JavaCameraView implements PictureCallback {
    private static final String TAG = "CameraView";
    private String mPictureFileName;
    private SQLiteDatabase.CursorFactory factory;
    private SQLiteDatabase openCVdb;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SQLiteDatabase.CursorFactory factory = new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                return null;
            }
        };
        DatabaseErrorHandler dbHandler = new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {

            }
        };
        DBHelper DBhelp = new DBHelper(context,factory,dbHandler);
        openCVdb = DBhelp.getWritableDatabase();


    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<android.hardware.Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(android.hardware.Camera.Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public android.hardware.Camera.Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        Mat image = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

        //These are the key functions for color and shape data
        getStatistics(image);
        edgeDetection(image);
        circleDetection(image);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }

    //@post: print mean, stdev of RGB and HSV (SLOW)
    public void getStatistics(Mat image) {
        //H: 0-180; S, V: 0-255
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        ArrayList<Double[]> bgrPixels = new ArrayList<>();
        ArrayList<Double[]> hsvPixels = new ArrayList<>();
        for (int row = 0; row < image.rows(); row++) {
            for (int col = 0; col < image.cols(); col++) {
                double[] bgr = image.get(row, col);
                double[] hsv = hsvImage.get(row, col);

                Double[] currentPixel = new Double[bgr.length];
                Double[] currentPixelHSV = new Double[hsv.length];

                for (int i = 0; i < bgr.length; i++) currentPixel[i] = new Double(bgr[i]);
                for (int i = 0; i < hsv.length; i++) currentPixelHSV[i] = new Double(hsv[i]);

                bgrPixels.add(currentPixel);
                hsvPixels.add(currentPixelHSV);
            }
        }

        System.out.println("BGR Mean: " + Arrays.toString(PhotoMath.pixelMean(bgrPixels)) );
        System.out.println("BGR STDEV: " + Arrays.toString(PhotoMath.pixelStdev(bgrPixels)));

        System.out.println("HSV Mean: " + Arrays.toString(PhotoMath.pixelMean(hsvPixels)));
        System.out.println("HSV STDEV: " + Arrays.toString(PhotoMath.pixelStdev(hsvPixels)));




        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 1, " + PhotoMath.pixelMean(bgrPixels)[2] + ", " + PhotoMath.pixelStdev(bgrPixels)[2] + ");");
        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 2, " + PhotoMath.pixelMean(bgrPixels)[1] + ", " + PhotoMath.pixelStdev(bgrPixels)[1] + ");");
        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 3, " + PhotoMath.pixelMean(bgrPixels)[0] + ", " + PhotoMath.pixelStdev(bgrPixels)[0] + ");");
        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 4, " + PhotoMath.pixelMean(hsvPixels)[0] + ", " + PhotoMath.pixelStdev(hsvPixels)[0] + ");");
        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 5, " + PhotoMath.pixelMean(hsvPixels)[1] + ", " + PhotoMath.pixelStdev(hsvPixels)[1] + ");");
        openCVdb.execSQL("insert into ColorStatistics values(" + image + ", 6, " + PhotoMath.pixelMean(hsvPixels)[2] + ", " + PhotoMath.pixelStdev(hsvPixels)[2] + ");");









        System.out.println(hsvImage.rows() + " --- " + hsvImage.cols());
    }

    //@post: print R, theta (polar coordinates descriptions) of each line in image
    public void edgeDetection(Mat src) {
        Mat dst = new Mat();
        Mat cdst = new Mat();

        //Detect edges with canny detector, then convert into BGR (50, 200 are threshholds)
        Imgproc.Canny(src, dst, 50, 200);
//        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        Mat lines = new Mat();
        double rho = 1;
        double theta = Math.PI/180;
        double minTheta = theta/2;
        double maxTheta = theta*2;
        Imgproc.HoughLines(dst, lines, rho, theta, 100, 0, 0, minTheta, maxTheta);
        //rows = numLines; cols == 1 iff > 0 lines
        System.out.println(lines.rows() + " --- " + lines.cols());
        if (lines.cols() == 1) {
            for (int row = 0; row < lines.rows(); row++) {
                double[] rTheta = lines.get(row, 0);
                System.out.println("Rtheta: " + Arrays.toString(rTheta));
                openCVdb.execSQL("insert into PhotoShape values(" + src + ", 1, " + rTheta[0] + ", " + rTheta[1] + ");");
            }
        }
    }

    //@post: print Xcenter, Ycenter, radius of each circle
    public void circleDetection(Mat src) {
        Mat src_gray = new Mat();
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        //The tutorial uses a Gaussian Blur to lower noise, but this led to ignoring obvious circles
//            Imgproc.GaussianBlur(src_gray, src_gray, new org.opencv.core.Size(9, 9), 2, 2 );

        Mat circles = new Mat();
        double minDistCenters = src_gray.rows() / 16;
        double edgeUpperThreshold = 500;
        double centerDetectionThreshold = 50;
        Imgproc.HoughCircles(src_gray, circles, Imgproc.HOUGH_GRADIENT, 1,
                minDistCenters, edgeUpperThreshold, centerDetectionThreshold, 0, 0);
        System.out.println(circles.rows() + " --- " + circles.cols());
        if (circles.rows() == 1) {
            for (int col = 0; col < circles.cols(); col++) {
                System.out.println("---X,Y,Radius: " + Arrays.toString(circles.get(0, col)));
                openCVdb.execSQL("insert into PhotoShape values(" + src + ", 2, " + circles.get(0, col)[0] + ", " + circles.get(0, col)[2] + ");");
            }
        }
    }
}
