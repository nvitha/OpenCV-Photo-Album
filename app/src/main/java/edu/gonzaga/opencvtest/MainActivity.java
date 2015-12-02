package edu.gonzaga.opencvtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.gonzaga.opencvtest.R;

public class MainActivity extends Activity {
    private static final String TAG = "Main";

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
