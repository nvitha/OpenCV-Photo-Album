package edu.gonzaga.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.database.sqlite.*;
import android.widget.Spinner;
import android.widget.TextView;

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


        // populate images, set index to 0
        populatePicturesDefault();
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

        //Populate query spinner
        Spinner spinner = (Spinner) findViewById(R.id.selectSpinner);
        //TODO: Selecting item in adapter should trigger a query
        //These are the query names
        String[] vals = new String[] {"default ordering",
                "sort by average red",
                "sort by average green",
                "sort by average blue"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //set on click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                System.out.println(pos);
                //THIS IS WHERE YOU DECIDE WHAT QUERY TO EXECUTE
                //Should execute the query associated with pos and change the pictures ArrayList appropriately
                //Pictures should be populated with the results of the query, and currentIndex should reset to 1
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing!
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

    public void updateShownIndex() {
        TextView ind = (TextView) findViewById(R.id.picIndex);
        String val = (currentPictureIndex+1) + " / " + pictures.size();
        ind.setText(val);
    }

    public void setImageToCurrent() {
        ImageView img = (ImageView) findViewById(R.id.imageView);
        Bitmap bmp = BitmapFactory.decodeFile(pictures.get(currentPictureIndex).toString());
        img.setImageBitmap(bmp);
        updateShownIndex();
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
