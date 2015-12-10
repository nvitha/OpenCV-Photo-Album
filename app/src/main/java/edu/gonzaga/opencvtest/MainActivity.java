package edu.gonzaga.opencvtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
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
import android.widget.Toast;
//import android.database.sqlite.*;
import static android.database.sqlite.SQLiteDatabase.CREATE_IF_NECESSARY;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static android.database.sqlite.SQLiteDatabase.openDatabase;
import static android.database.sqlite.SQLiteDatabase.CursorFactory;

import edu.gonzaga.opencvtest.R;

public class MainActivity extends Activity {
    private static final String TAG = "Main";
    private ArrayList<File> pictures; //should be sorted
    private int currentPictureIndex;
    private SQLiteDatabase openCVdb;
    //MainActivity instance = new MainActivity();
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
        Context contextNew = this;
        toSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Start snap photo activity");
                Intent intent = new Intent(main, PhotoActivity.class);
                startActivity(intent);
            }
        });
        initializeSQLiteDB();

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
            if(f == null){return;}
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
        if(pictures == null){return;}
        if(currentPictureIndex == 0){return;}
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

    // Create SQLLite database connection for executing SQL queries
    public void initializeSQLiteDB(){
        SQLiteDatabase.CursorFactory factory = new CursorFactory() {
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
        DBHelper DBhelp = new DBHelper(this.getApplicationContext(),factory,dbHandler);

        openCVdb = DBhelp.getWritableDatabase();
        toastTest("Database Created or Loaded");
/*      openCVdb.execSQL("create table if not exists Photo(PhotoID int PRIMARY KEY NOT NULL, FileLocation varchar(255) NOT NULL);");
        openCVdb.execSQL("create table if not exists ColorScheme(ColorSchemeID int PRIMARY KEY NOT NULL, ColorSchemeName varchar(255) NOT NULL);");
        openCVdb.execSQL("create table if not exists ColorSchemeComponent(ColorSchemeID int PRIMARY KEY NOT NULL, ColorSchemeComponentID int NOT NULL, ColorSchemeComponentName varchar(255) NOT NULL);");
        openCVdb.execSQL("create table if not exists ColorStatistics(PhotoID int PRIMARY KEY NOT NULL, ColorSchemeComponentID int NOT NULL, AverageValue int NOT NULL, STDEV int NOT NULL, Check (AverageValue > 0 AND AverageValue < 361));");
        openCVdb.execSQL("create table if not exists  Shape(ShapeID int PRIMARY KEY NOT NULL, ShapeName varchar(255) NOT NULL);");
        openCVdb.execSQL("create table if not exists PhotoShape(PhotoID int NOT NULL, ShapeID int NOT NULL, Loc1 varchar(255) NOT NULL, Loc2 varchar(255) NOT NULL);");
*/
/*    openCVdb.execSQL("create view SingleColorView AS\n" +
                "(select PhotoID, ColorSchemeName, ColorSchemeComponentName, AverageValue, STDEV\n" +
                "from ColorSchemeComponent \n" +
                "natural join ColorScheme \n" +
                "natural join ColorStatistics\n" +
                "order by PhotoID);");
        openCVdb.execSQL("create view ColorView AS\n" +
                "(select Photo.PhotoID PhotoID, Photo.FileLocation FileLocation, r.AverageValue Red, r.STDEV RedStdev, g.AverageValue Green, g.STDEV GreenStdev, b.AverageValue Blue, b.STDEV BlueStdev, h.AverageValueHue, h.STDEV HueStdev, s.AverageValue Saturation, s.STDEV SaturationStdev, s.AverageValue Value, v.STDEV ValueStdev\n" +
                "from Photo\n" +
                "join SingleColorView r on Photo.PhotoID=r.PhotoID\n" +
                "join SingleColorView g on Photo.PhotoID=g.PhotoID\n" +
                "join SingleColorView b on Photo.PhotoID=b.PhotoID\n" +
                "join SingleColorView h on Photo.PhotoID=h.PhotoID\n" +
                "join SingleColorView s on Photo.PhotoID=s.PhotoID\n" +
                "join SingleColorView v on Photo.PhotoID=v.PhotoID\n" +
                "having r.ColorSchemeComponentName = \"Red\"\n" +
                "and g.ColorSchemeComponentName = \"Green\"\n" +
                "and b.ColorSchemeComponentName = \"Blue\"\n" +
                "and h.ColorSchemeComponentName = \"Hue\"\n" +
                "and s.ColorSchemeComponentName = \"Saturation\"\n" +
                "and v.ColorSchemeComponentName = \"Value\"\n" +
                "order by Photo.PhotoID);");
        openCVdb.execSQL("create view SingleShapeView as\n" +
                "(select PhotoID, ShapeID, ShapeName, Count(*) Occurence\n" +
                "from Shape \n" +
                "natural join PhotoShape\n" +
                "group by PhotoID, ShapeID\n" +
                "order by PhotoID);");
*/
        // Execute create/insertion commands; Would be used for inserting statistics as well
 /*       openCVdb.execSQL("insert into ColorScheme values(1,RGB), (2,HSV);");
        openCVdb.execSQL("insert into ColorSchemeComponent values (1,1,Red), (1,2,Green), (1,3,Blue), (2,4,Hue), (2,5,Saturation), (2,6,Value);");
        openCVdb.execSQL("insert into Shape values (1,Line), (2,Circle);");
 */
    }

    public void toastTest(String string){
        Context context = getApplicationContext();
        CharSequence text = string;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
