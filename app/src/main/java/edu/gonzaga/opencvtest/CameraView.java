package edu.gonzaga.opencvtest;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

import static org.opencv.imgcodecs.Imgcodecs.imdecode;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "CameraView";
    private String mPictureFileName;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
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

        //stats
//        Mat m = new Mat()
//        MatOfByte m = new MatOfByte(data);
        Mat image = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
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
        //TODO: These mean/stdev calls are really slow (~30 seconds on Moto E)
        // Optimize, or move to a different thread
        System.out.println("BGR Mean: " + Arrays.toString(PhotoMath.pixelMean(bgrPixels)) );
        System.out.println("BGR STDEV: " + Arrays.toString(PhotoMath.pixelStdev(bgrPixels)));

        System.out.println("HSV Mean: " + Arrays.toString(PhotoMath.pixelMean(hsvPixels)) );
        System.out.println("HSV STDEV: " + Arrays.toString(PhotoMath.pixelStdev(hsvPixels)));

        System.out.println(hsvImage.rows() + " --- " + hsvImage.cols());

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }
}
