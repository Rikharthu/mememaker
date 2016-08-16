package com.teamtreehouse.mememaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

// this class takes care of opening the database if it exists,
// creating it if it does not, and upgrading it as necessary
public class MemeSQLiteHelper extends SQLiteOpenHelper  {

    private static final String DB_NAME = "memes.db";
    // used to version the database
    private static final int DB_VERSION = 2;

    //Meme Table functionality. Prepared statements
    public static final String MEMES_TABLE = "MEMES";
    public static final String COLUMN_MEME_ASSET = "ASSET";
    public static final String COLUMN_MEME_NAME = "NAME";
    public static final String COLUMN_MEME_CREATE_DATE = "CREATE_DATE";
    private static String CREATE_MEMES =
    "CREATE TABLE " + MEMES_TABLE + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MEME_ASSET +" TEXT," +
            COLUMN_MEME_NAME + " TEXT," +
            COLUMN_MEME_CREATE_DATE+" INTEGER)";

    //Meme Table Annotations functionality
    public static final String ANNOTATIONS_TABLE = "ANNOTATIONS";
    public static final String COLUMN_ANNOTATION_COLOR = "COLOR";
    public static final String COLUMN_ANNOTATION_X = "X";
    public static final String COLUMN_ANNOTATION_Y = "Y";
    public static final String COLUMN_ANNOTATION_TITLE = "TITLE";
    public static final String COLUMN_FOREIGN_KEY_MEME = "MEME_ID";
    private static final String CREATE_ANNOTATIONS = "CREATE TABLE " + ANNOTATIONS_TABLE + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ANNOTATION_X + " INTEGER, " +
            COLUMN_ANNOTATION_Y + " INTEGER, " +
            COLUMN_ANNOTATION_TITLE + " TEXT, " +
            COLUMN_ANNOTATION_COLOR + " TEXT, " +
            COLUMN_FOREIGN_KEY_MEME + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_FOREIGN_KEY_MEME + ") REFERENCES MEMES(_ID))";
    // set ANNOtATION's memes foreign key to refer MEMES id field

    public MemeSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // we pass DB_VERSION to check current database version
        // and call onUpgrade or onDowngrade if necessary
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // execute passed SQL statements
        sqLiteDatabase.execSQL(CREATE_MEMES);
        sqLiteDatabase.execSQL(CREATE_ANNOTATIONS);
    }

    private static final String ALTER_ADD_CREATE_DATE="ALTER TABLE "+MEMES_TABLE+
            " ADD COLUMN " +COLUMN_MEME_CREATE_DATE+" INTEGER";
    // depending on old and new versions, alter database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch(oldVersion){
            // we want all updates, so no break statements
            case 1:
                // sql to execute
                sqLiteDatabase.execSQL(ALTER_ADD_CREATE_DATE);
        }
    }
}
