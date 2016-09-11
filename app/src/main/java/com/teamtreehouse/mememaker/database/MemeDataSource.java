package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;
import java.util.Date;

/** This is a helper class.
 Application will use this class to interract with the database
 SQL syntax is used ONLY here and in SQLiteHelper (like FileUtilities)
 Just for separation of concerns
*/
public class MemeDataSource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqliteHelper;

    public MemeDataSource(Context context) {

        mContext = context;
        mMemeSqliteHelper = new MemeSQLiteHelper(context);
    }

    // open db for writing
    private SQLiteDatabase open() {
        return mMemeSqliteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database) {
        database.close();
    }

    /**
     * 1) get memes
     * 2) add annotations
     * @return
     */
    public ArrayList<Meme> read() {
        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);
        return memes;
    }

    /**
     * Fetches all memes (without annotations)
     * @return
     */
    public ArrayList<Meme> readMemes() {
        SQLiteDatabase database = open();

        Cursor cursor = database.query(
                MemeSQLiteHelper.MEMES_TABLE,
                new String [] {MemeSQLiteHelper.COLUMN_MEME_NAME, BaseColumns._ID, MemeSQLiteHelper.COLUMN_MEME_ASSET},
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                MemeSQLiteHelper.COLUMN_MEME_CREATE_DATE+" DESC"); //order by date column, descending

        ArrayList<Meme> memes = new ArrayList<Meme>();
        // move cursor to first position and check that it is not empty
        if(cursor.moveToFirst()) {
            do {
                // construct Meme object from table data
                Meme meme = new Meme(getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_ASSET),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_NAME),
                        null);
                memes.add(meme);
                // move to next item in a cursor
                // returns true, while there are entries
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return memes;
    }

    /**
     * Pulls annotations for all memes and attaches em
     * @param memes
     */
    public void addMemeAnnotations(ArrayList<Meme> memes) {
        SQLiteDatabase database = open();

        for (Meme meme : memes) {
            ArrayList<MemeAnnotation> annotations = new ArrayList<MemeAnnotation>();
           // pull all annotations for meme's foreign key
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE +
                            " WHERE MEME_ID = " + meme.getId(), null);

            if(cursor.moveToFirst()) {
                do {
                    MemeAnnotation annotation = new MemeAnnotation(
                            getIntFromColumnName(cursor, BaseColumns._ID),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y));
                    annotations.add(annotation);
                } while(cursor.moveToNext());
            }
            meme.setAnnotations(annotations);
            cursor.close();
        }
        database.close();
    }

    public void delete(int memeId){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // implementation details
        database.delete(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                String.format("%s=%s",MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, String.valueOf(memeId)),
                null);
        database.delete(MemeSQLiteHelper.MEMES_TABLE,
                String.format("%s=%s",BaseColumns._ID, String.valueOf(memeId)),
                null);

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    public void update(Meme meme){
        SQLiteDatabase database = open();
        database.beginTransaction();

        // update memevalues
        ContentValues updateMemeValues = new ContentValues();
        updateMemeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME,meme.getName());
        database.update(MemeSQLiteHelper.MEMES_TABLE,
                updateMemeValues,
                String.format("%s=%d", BaseColumns._ID,meme.getId()),// WHERE clause
                null);
        // update this meme's annotations
        for(MemeAnnotation annotation:meme.getAnnotations()){
            ContentValues updateAnnotations = new ContentValues();
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, meme.getId());

            if(annotation.hasBeenSaved()){
                // annotation exists in DB and has an id => update
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        updateAnnotations,
                        String.format("%s=%d",BaseColumns._ID,annotation.getId()),
                        null);
            }else{
                // annotation does not exist in our DB => insert
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        null,
                        updateAnnotations);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        // get index to passed column
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    // create new Meme object in a database
    public void create(Meme meme) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        // This class is used to store a set of values that the ContentResolver can process.
        ContentValues memeValues = new ContentValues();
        // create a key-value pair
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET, meme.getAssetLocation());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_CREATE_DATE,new Date().getTime());
        // ContentValues's keys should be column names. values will be automatically mapped to according rows
        // returns primary key
        long memeID = database.insert(MemeSQLiteHelper.MEMES_TABLE, null, memeValues);

        // now save meme's annotations in a different table
        for (MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            // reuse memeID
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeID);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationValues);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }
}













