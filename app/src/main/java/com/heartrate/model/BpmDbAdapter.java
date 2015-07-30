package com.heartrate.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class BpmDbAdapter {

    private static final String TABLE_BPM = "table_bpm";
    private static final String COL_ID = "_id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_DATE = "DATE";
    private static final int NUM_COL_DATE = 1;
    private static final String COL_VALUE = "VALUE";
    private static final int NUM_COL_VALUE = 2;
    private static final String COL_EFFORT = "EFFORT";
    private static final int NUM_COL_EFFORT = 3;
    private static final String COL_HOW = "HOW";
    private static final int NUM_COL_HOW = 4;
    private static final String COL_USER = "USER";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_BPM + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DATE + " INTEGER, "
            + COL_VALUE + " INTEGER, "
            + COL_EFFORT + " INTEGER, "
            + COL_HOW + " INTEGER, "
            + COL_USER + " TEXT NOT NULL );";

    private static final String TAG = "BpmDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BPM);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public BpmDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    public BpmDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @return rowId or -1 if failed
     */
    public long insertBpm(String user, long date, int val, int where, int effort, int how) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_USER, user);
        initialValues.put(COL_DATE, date);
        initialValues.put(COL_VALUE, val);
        initialValues.put(COL_EFFORT, effort);
        initialValues.put(COL_HOW, how);

        return mDb.insert(TABLE_BPM, null, initialValues);
    }

    public long insertBpm(String user, RegisteredBpm f) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_USER, user);
        initialValues.put(COL_DATE, f.getDate());
        initialValues.put(COL_VALUE, f.getValue());
        initialValues.put(COL_EFFORT, f.getEffort());
        initialValues.put(COL_HOW, f.getHow());

        return mDb.insert(TABLE_BPM, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteBpm(long rowId) {

        return mDb.delete(TABLE_BPM, COL_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllBpm(String user) {

        return mDb.query(TABLE_BPM, new String[]{COL_ID, COL_DATE, COL_VALUE,
                COL_EFFORT, COL_HOW}, COL_USER + " LIKE \"" + user + "\"", null, null, null, null, null);
    }


    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateBpm(long rowId, int effort, int how) {
        ContentValues args = new ContentValues();
        args.put(COL_EFFORT, effort);
        args.put(COL_HOW, how);

        return mDb.update(TABLE_BPM, args, COL_ID + "=" + rowId, null) > 0;
    }


    public ArrayList<RegisteredBpm> getAllBpmUser(String user) {
        ArrayList<RegisteredBpm> listFcs = new ArrayList<RegisteredBpm>();
        //obtain in a Cursor HR value contained in BDD
        Cursor c = mDb.query(TABLE_BPM, new String[]{COL_ID, COL_DATE, COL_VALUE, COL_EFFORT, COL_HOW}, COL_USER + " LIKE \"" + user + "\"", null, null, null, null);
        while (c.moveToNext()) {
            listFcs.add(cursorToBpm(c));
        }
        //On ferme le cursor
        c.close();
        return listFcs;
    }

    public int getBpmMin(String user) {
        //	Cursor c = mDb.query(TABLE_FC, new String [] {COL_VALUE+"=(SELECT MIN("+COL_VALUE+"))"}, COL_USER + " LIKE \"" + user +"\"", null, null, null, null);
        Cursor c = mDb.query(TABLE_BPM, new String[]{"MIN(" + COL_VALUE + ")"}, COL_USER + " LIKE \"" + user + "\"", null, null, null, null);
        int val;
        c.moveToFirst();
        if (c.getCount() == 0)
            val = 0;
        else
            val = c.getInt(0);
        c.close();
        return val;

    }

    public int getBpmMax(String user) {
        //Cursor c = mDb.rawQuery("SELECT MAX("+COL_VALUE+") FROM "+TABLE_FC , null);
        //Cursor c = mDb.query(TABLE_FC, new String [] {COL_VALUE+"=(SELECT MAX("+COL_VALUE+"))"}, COL_USER + " LIKE \"" + user +"\"", null, null, null, null);
        Cursor c = mDb.query(TABLE_BPM, new String[]{"MAX(" + COL_VALUE + ")"}, COL_USER + " LIKE \"" + user + "\"", null, null, null, null);
        int val;
        c.moveToFirst();
        if (c.getCount() == 0)
            val = 0;
        else
            val = c.getInt(0);
        c.close();
        return val;
    }

    //this method aim to convert cursor in HR
    private RegisteredBpm cursorToBpm(Cursor c) {
        //null is return if nothing found in request
        if (c.getCount() == 0)
            return null;
        //create HR
        RegisteredBpm bpm = new RegisteredBpm();
        //thanks to Cursor we fill info for registered hr
        bpm.setId(c.getInt(NUM_COL_ID));
        bpm.setDate(c.getLong(NUM_COL_DATE));
        bpm.setValue(c.getInt(NUM_COL_VALUE));
        bpm.setEffort(c.getInt(NUM_COL_EFFORT));
        bpm.setHow(c.getInt(NUM_COL_HOW));

        return bpm;
    }

}
