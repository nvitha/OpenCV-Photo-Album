package edu.gonzaga.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.database.sqlite.*;

import java.io.File;
import java.util.ArrayList;

import edu.gonzaga.opencvtest.R;

public class MainActivity extends Activity {
    private static final String TAG = "Main";
    private ArrayList<File> pictures; //should be sorted
    private int currentPictureIndex;
    //TODO: Pass images to pictures from SQL query

    public MainActivity() {
        Log.i(TAG, "Start main");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toSnap = (Button) findViewById(R.id.snapButton);
        final Activity main = this;
        toSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Start snap photo activity");
                Intent intent = new Intent(main, PhotoActivity.class);
                startActivity(intent);
            }
        });


        // populate images
        populatePicturesDefault();
        //TODO: Populate pictures with results of SQL query
        //set listeners
        Button nextButton = (Button) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("nextButton was clicked");
                nextPhoto();
            }
        });
        Button prevButton = (Button) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPhoto();
            }
        });

        setImageToCurrent();
    }

    public void populatePicturesDefault() {
        pictures = new ArrayList<>();
        currentPictureIndex = 0;
        String dirName = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(dirName);

        for (File f : dir.listFiles()) {
            if (f.toString().endsWith(".jpg")) pictures.add(f);
        }
    }

    public void setImageToCurrent() {
        ImageView img = (ImageView) findViewById(R.id.imageView);
        Bitmap bmp = BitmapFactory.decodeFile(pictures.get(currentPictureIndex).toString());
        img.setImageBitmap(bmp);
    }

    public void nextPhoto() {
        if (currentPictureIndex < pictures.size() - 1) currentPictureIndex++;
        System.out.println("Index: " + currentPictureIndex);
        setImageToCurrent();
    }

    public void prevPhoto() {
        currentPictureIndex = Math.max(0, currentPictureIndex - 1);
        setImageToCurrent();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
