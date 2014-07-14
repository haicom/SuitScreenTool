package com.suitscreen.app.providers;

import android.net.Uri;
import android.provider.BaseColumns;


public class SuitScreen {
    private final static String TAG = "SuitScreen";
    /** The authority for the contacts provider */
    public static final String AUTHORITY = "suitscreenapp";
    /** A content:// style uri to the authority for the contacts provider */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    // Constructor
    public SuitScreen() {

    }
    
    public static final class InstalledApp implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(AUTHORITY_URI, "install");
        
       
        public static final String PACKAGE_NAME = "package_name";
        public static final String CLASS_NAME   = "class_name";        
        public static final String ACTIVITY_START = "activity_start";       
        
        /**
         * The "deleted" flag: "0" by default, "1" if the row has been marked
         * for deletion.
         * <P>Type: INTEGER</P>
         */
        public static final String APP_DELETED = "deleted";
        
        public static final String DATE   = "date";
        
      
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of
         * people.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/install";

        /**
         * The MIME type of a {@link #CONTENT_URI} subdirectory of a single
         * person.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/install";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "date ASC ";

    }    
  
}
