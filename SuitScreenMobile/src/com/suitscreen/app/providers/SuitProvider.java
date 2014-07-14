package com.suitscreen.app.providers;



import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.suitscreen.app.mobiles.SocketService;
import com.suitscreen.app.providers.SuitScreen.InstalledApp;




public class SuitProvider extends  SQLiteContentProvider {
    private final static String TAG = "BreezingProvider";
    public  static String TABLE_INSTALL_APP = "install_app";    
 
    private SQLiteOpenHelper mOpenHelper;

    @Override
    public String getType(Uri url) {
        Log.d(TAG, " getType uri =   " + url);
        // Generate the body of the query.
        int match = sURLMatcher.match(url);
        switch (match) {
            case SUIT_APP_INSTALL:
                return InstalledApp.CONTENT_TYPE;
            case SUIT_APP_INSTALL_ID:
                return InstalledApp.CONTENT_ITEM_TYPE;         
            default:
                return null;
        }
    }

    private void notifyChange(Uri uri) {
        Log.d(TAG, " notifyChange uri = " + uri);
        ContentResolver cr = getContext().getContentResolver();
        cr.notifyChange(uri, null);
    }

    @Override
    public boolean onCreate() {
        super.onCreate();
        try {
            return initialize();
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot start provider", e);
            return false;
        }
    }

    private boolean initialize() {
       
        mOpenHelper = getDatabaseHelper();
        mDb = mOpenHelper.getWritableDatabase();
        return (mDb != null);
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // Generate the body of the query.
        int match = sURLMatcher.match(url);
        switch (match) {
            case SUIT_APP_INSTALL:
                qb.setTables(TABLE_INSTALL_APP);
                break;
            case SUIT_APP_INSTALL_ID:
                qb.setTables(TABLE_INSTALL_APP);
                qb.appendWhere("( _id = " + url.getPathSegments().get(1) + ")");
                break;
            
          
            default:
                Log.e(TAG, "query: invalid request: " + url);
                return null;
        }

        String finalSortOrder = InstalledApp.DEFAULT_SORT_ORDER;
      

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection,
                selectionArgs, null, null, finalSortOrder);
       
        Log.d(SocketService.TAG, " query  selection = " + selection.toString()  +  " selectionArgs = " + selectionArgs.toString() );
        ret.setNotificationUri(getContext().getContentResolver(), url);
        return ret;
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        return super.delete(url, where, whereArgs);
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        return super.insert(url, initialValues);
    }

    @Override
    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        Log.d(SocketService.TAG, " update  where = " + where.toString()  +  " whereArgs = " + whereArgs.toString() );
        return super.update(url, values, where, whereArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        Log.d(TAG, " applyBatch ");
        return super.applyBatch(operations);
    }

    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context) {
        return SuitDatabaseHelper.getInstance(getContext());
    }

    @Override
    protected Uri insertInTransaction(Uri url, ContentValues initialValues) {
        long rowID = 0;
        int match = sURLMatcher.match(url);
        String dateStr = simpleDateFormat("yyyyMMddHHmmss");
        
        initialValues.put(InstalledApp.DATE, dateStr);
       

        switch (match) {
            case SUIT_APP_INSTALL:
                rowID = mDb.insert(TABLE_INSTALL_APP, InstalledApp.PACKAGE_NAME, initialValues);
                break;          
            default:
                Log.e(TAG, "insert: invalid request: " + url);
                return null;
        }

        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(url, rowID);

            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "insert " + uri + " succeeded");
            }
            notifyChange(url);
            return uri;
        } else {
            Log.e(TAG,"insert: failed! " + initialValues.toString() );
        }

        return null;
    }

   

    @Override
    protected int updateInTransaction(Uri url, ContentValues values, String where,
                String[] whereArgs) {
        int count = 0;
        String table = null;
        String extraWhere = null;

        int match = sURLMatcher.match(url);
        switch (match) {
            case SUIT_APP_INSTALL:                
                table = TABLE_INSTALL_APP;
                break;
            case SUIT_APP_INSTALL_ID:                
                table = TABLE_INSTALL_APP;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;           
            default:
                throw new UnsupportedOperationException(
                        "URI " + url + " not supported");
        }

        if (extraWhere != null) {
            where = DatabaseUtils.concatenateWhere(where, extraWhere);
        }

        Log.d(TAG, "update where = " + where);
        count = mDb.update(table, values, where, whereArgs);

        if (count > 0) {
            Log.d(TAG, "update " + url + " succeeded");
            notifyChange(url);
        }

        return count;
    }

    @Override
    protected int deleteInTransaction(Uri url, String where,
            String[] whereArgs) {
        int count = 0;
        int match = sURLMatcher.match(url);


        switch (match) {
            case SUIT_APP_INSTALL:
                
                count = mDb.delete(TABLE_INSTALL_APP, where, whereArgs);
                break;
            case SUIT_APP_INSTALL_ID:
                
                int appId;
    
                try {
                    appId = Integer.parseInt( url.getPathSegments().get(1) );
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }
    
                where = DatabaseUtils.concatenateWhere("_id = " + appId, where);
                count = mDb.delete(TABLE_INSTALL_APP, where, whereArgs);
                break;         
            default:
                Log.e(TAG, "query: invalid request: " + url);
        }

        return count;
    }

    @Override
    protected void notifyChange() {
        ContentResolver cr = getContext().getContentResolver();
        //cr.notifyChange(uri, null);
    }
    
    private static final int SUIT_APP_INSTALL = 1;
    private static final int SUIT_APP_INSTALL_ID = 2;    
  

    private static final UriMatcher sURLMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI(SuitScreen.AUTHORITY, "install", SUIT_APP_INSTALL);
        sURLMatcher.addURI(SuitScreen.AUTHORITY, "install/#", SUIT_APP_INSTALL_ID);
    
    }
    
   
    private String simpleDateFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String strDate = sdf.format(new Date());
        Log.d(TAG, "format = " + format + " sdf.format(new Date())  = " +  sdf.format(new Date()));
        Date date = null;
        try {
            date =  sdf.parse(strDate);
        } catch (ParseException e) {           
            e.printStackTrace();
        }

        Log.d(TAG, " simpleDateFormat date = " + date + " strDate = " + strDate);
//        int intDate = String.v.parseInt(strDate);
//        Log.d(TAG, "simpleDateFormat longDate = " + intDate);
//        return intDate;
        return strDate;
    }

}
