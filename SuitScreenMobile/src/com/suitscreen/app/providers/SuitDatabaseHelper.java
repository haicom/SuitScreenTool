package com.suitscreen.app.providers;

import com.suitscreen.app.providers.SuitScreen.InstalledApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SuitDatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "BreezingDatabaseHelper";
    private static SuitDatabaseHelper sInstance = null;
    static final String DATABASE_NAME = "suitscreen.db";
    static final int DATABASE_VERSION = 1;
    private final Context mContext;



    private SuitDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Return a singleton helper for the combined Breezing health
     * database.
     */
    /* package */
    static synchronized SuitDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SuitDatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createInstallAppTables(db);
    }
    
    
    private void createInstallAppTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SuitProvider.TABLE_INSTALL_APP  + " ("
              +  InstalledApp._ID +  " INTEGER PRIMARY KEY, "
              +  InstalledApp.PACKAGE_NAME + " TEXT NOT NULL , "
              +  InstalledApp.CLASS_NAME +  " TEXT NOT NULL , "
              +  InstalledApp.ACTIVITY_START +  "   BOOL NOT NULL DEFAULT 0 ," 
              +  InstalledApp.APP_DELETED +  " BOOL NOT NULL DEFAULT 0 ," 
              +  InstalledApp.DATE + " TEXT NOT NULL " +
                   ");");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, " onUpgrade oldVersion = " + oldVersion + " newVersion = " + newVersion);    
    }

   

}
