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
        db.execSQL("create table if not exists Photo(PhotoID int, FileLocation varchar(255));");
        db.execSQL("create table if not exists ColorScheme(ColorSchemeID int PRIMARY KEY,ColorSchemeName varchar(255));");
        db.execSQL("create table if not exists ColorSchemeComponent(ColorSchemeID int, ColorSchemeComponentID int, ColorSchemeComponentName varchar(255));");
        db.execSQL("create table if not exists ColorStatistics(PhotoID int, ColorSchemeComponentID int, AverageValue REAL, STDEV REAL, UNIQUE(PhotoID,ColorSchemeComponentID));");
        db.execSQL("create table if not exists Shape(ShapeID int PRIMARY KEY, ShapeName varchar(255));");
        db.execSQL("create table if not exists PhotoShape(PhotoID int, ShapeID int, Loc1 int, Loc2 int, Unique(PhotoID,ShapeID,Loc1,Loc2));");

        db.execSQL("insert into ColorScheme (ColorSchemeID,ColorSchemeName) Values(1,'RGB'), (2,'HSV');");
        db.execSQL("insert into ColorSchemeComponent (ColorSchemeID, ColorSchemeComponentID, ColorSchemeComponentName) Values(1,1,'Red'), (1,2,'Green'), (1,3,'Blue'), (2,4,'Hue'), (2,5,'Saturation'),(2,6,'Value');");
        db.execSQL("insert into Shape (ShapeID, ShapeName) Values(1,'Line'),(2,'Circle');");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Photo");
        db.execSQL("drop table if exists ColorScheme");
        db.execSQL("drop table if exists ColorSchemeComponent");
        db.execSQL("drop table if exists ColorStatistics");
        db.execSQL("drop table if exists Shape");
        db.execSQL("drop table if exists PhotoShape");

        /*db.execSQL("create table if not exists Photo(PhotoID int, FileLocation varchar(255));");
        db.execSQL("create table if not exists ColorScheme(ColorSchemeID int PRIMARY KEY,ColorSchemeName varchar(255));");
        db.execSQL("create table if not exists ColorSchemeComponent(ColorSchemeID int PRIMARY KEY, ColorSchemeComponentID int, ColorSchemeComponentName varchar(255));");
        db.execSQL("create table if not exists ColorStatistics(PhotoID int, ColorSchemeComponentID int, AverageValue REAL, STDEV REAL, UNIQUE(PhotoID,ColorSchemeComponentID));");
        db.execSQL("create table if not exists Shape(ShapeID int PRIMARY KEY, ShapeName varchar(255));");
        db.execSQL("create table if not exists PhotoShape(PhotoID int, ShapeID int, Loc1 int, Loc2 int, Unique(PhotoID,ShapeID,Loc1,Loc2));");

        db.execSQL("insert into ColorScheme (ColorSchemeID,ColorSchemeName) Values(1,'RGB'), (2,'HSV');");
        db.execSQL("insert into ColorSchemeComponent (ColorSchemeID, ColorSchemeComponentID, ColorSchemeComponentName) Values(1,1,'Red'), (1,2,'Green'), (1,3,'Blue'), (2,4,'Hue'), (2,5,'Saturation'),(2,6,'Value');");
        db.execSQL("insert into Shape (ShapeID, ShapeName) Values(1,'Line'),(2,'Circle');");*/
    }

    public void destroy(SQLiteDatabase db){
        db.execSQL(
                "delete table if exists Photo;\n"
                        + "drop table if exists ColorScheme;\n"
                        + "drop table if exists ColorSchemeComponent;\n"
                        + "drop table if exists ColorStatistics;\n"
                        + "drop table if exists Shape;\n"
                        + "drop table if exists PhotoShape;\n"
        );
    }

    public boolean exists(){
        return true;
    }
}
