package edu.gonzaga.opencvtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "OpenCV.db";
    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler dbHandler){
        super(context, DATABASE_NAME , factory, 1, dbHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table Photo(PhotoID int, FileLocation varchar(255));"
                        + "create table ColorScheme(ColorSchemeID int,ColorSchemeName varchar(255));"
                        + "create table ColorSchemeComponent(ColorSchemeID int, ColorSchemeComponentID int,ColorSchemeComponentName varchar(255));"
                        + "create table ColorStatistics(PhotoID int, ColorSchemeComponentID int, AverageValue int, STDEV int, Check(AverageValue > 0 AND AverageValue < 361));"
                        + "create table Shape(ShapeID int, ShapeName varchar(255));"
                        + "create table PhotoShape(PhotoID int, ShapeID int, Loc1 int, Loc2 int);"
                        + "insert into ColorScheme Values(1,RGB), (2,HSV);"
                        + "insert into ColorSchemeComponent Values(1,1,Red), (1,2,Green), (1,3,Blue), (2,4,Hue), (2,5,Saturation),(2,6,Value);"
                        + "insert into Shape Values(1,Line),(2,Circle);"
        );
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To be added if we add different versions
    }
}
