package de.hhn.aib.swlab.wise1920.group05.exercise1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class AlarmContract {

    private AlarmContract() {
    }

    public static class Alarms implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";
        public static final String COLUMN_NAME_ACTIVE = "active";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Alarms.TABLE_NAME + " (" +
                    Alarms._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Alarms.COLUMN_NAME_NAME + " TEXT," +
                    Alarms.COLUMN_NAME_HOUR + " INTEGER," +
                    Alarms.COLUMN_NAME_MINUTE + " INTEGER," +
                    Alarms.COLUMN_NAME_ACTIVE + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Alarms.TABLE_NAME;

    public static class AlarmsDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Alarms.db";

        public AlarmsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion,newVersion);
        }
    }
}
