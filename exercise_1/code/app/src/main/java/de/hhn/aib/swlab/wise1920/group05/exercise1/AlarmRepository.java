package de.hhn.aib.swlab.wise1920.group05.exercise1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmRepository {

    private MutableLiveData<List<Alarm>> liveDataAlarms;
    private final AlarmContract.AlarmsDbHelper alarmDbHelper;


    public AlarmRepository(Context context) {
        alarmDbHelper = new AlarmContract.AlarmsDbHelper(context);
    }

    /**
     * Updates the Alarm-List and the Alarm-LiveDataList to the current version in the DB.
     * @return updated List<Alarm> alarms
     */
    public List<Alarm> getAlarms() {
        SQLiteDatabase dbRead = alarmDbHelper.getReadableDatabase();
        String sortOrder = AlarmContract.Alarms.COLUMN_NAME_HOUR + " ASC"; //sort results
        Cursor cursor = dbRead.rawQuery(" SELECT * FROM " + AlarmContract.Alarms.TABLE_NAME + " ORDER BY " + sortOrder, null);

        List<Alarm> alarms = createAlarmListFromDbCursor(cursor);
        liveDataAlarms = new MutableLiveData<>();
        liveDataAlarms.setValue(alarms);
        return alarms;
    }

    public LiveData<List<Alarm>> getLiveDataAlarms() {
        getAlarms();
        return liveDataAlarms;
    }

    public long addAlarm(String alarmName, int alarmHour, int alarmMinute){
        SQLiteDatabase dbWrite = alarmDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlarmContract.Alarms.COLUMN_NAME_NAME, alarmName);
        values.put(AlarmContract.Alarms.COLUMN_NAME_HOUR, alarmHour);
        values.put(AlarmContract.Alarms.COLUMN_NAME_MINUTE, alarmMinute);
        values.put(AlarmContract.Alarms.COLUMN_NAME_ACTIVE, 1);
        return dbWrite.insert(AlarmContract.Alarms.TABLE_NAME, null, values);
    }

    public Alarm getLastAddedAlarm(){
        return getAlarms().get(getAlarms().size() - 1);
    }

    public void deleteAlarm(Alarm alarm){
        String selection = AlarmContract.Alarms._ID + " = ? ";
        String[] selectionArgs = { alarm.getId() + "" };
        SQLiteDatabase dbWrite = alarmDbHelper.getWritableDatabase();
        dbWrite.delete(AlarmContract.Alarms.TABLE_NAME, selection, selectionArgs);
    }


    public List<Alarm> getActiveAlarms() {
        SQLiteDatabase dbRead = alarmDbHelper.getReadableDatabase();
        ArrayList<Alarm> activeAlarms = new ArrayList<>();
        String[] selectionArgs = {"1"};
        String sortOrder = AlarmContract.Alarms.COLUMN_NAME_HOUR + " ASC"; //sort results
        Cursor cursor = dbRead.rawQuery(" SELECT * FROM " + AlarmContract.Alarms.TABLE_NAME + " WHERE " + AlarmContract.Alarms.COLUMN_NAME_ACTIVE + " = ? ORDER BY " + sortOrder, selectionArgs);
        if(cursor.getCount() == 0){
            return activeAlarms;
        } else {

            while(cursor.moveToNext()){
                Alarm alarm = new Alarm(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4));
                activeAlarms.add(alarm);
            }
        }
        return activeAlarms;
    }


     private List<Alarm> createAlarmListFromDbCursor(Cursor cursor){
        List<Alarm> alarmList = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                alarmList.add(new Alarm(cursor.getInt(cursor.getColumnIndex(AlarmContract.Alarms._ID)), cursor.getString(cursor.getColumnIndex("name")), cursor.getInt(cursor.getColumnIndex("hour")), cursor.getInt(cursor.getColumnIndex("minute")), cursor.getInt(cursor.getColumnIndex("active"))));
            }
        }
        return alarmList;
    }

    /**
     * Change the Activation Status of an Alarm in the DB
     * @param alarm from which the Activation Status should be changed
     * @param newActiveValue the new Activation Status (active = 1, inactive = 0) that should be stored
     */
    private void changeActivation(Alarm alarm, int newActiveValue){
        SQLiteDatabase dbWrite = alarmDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AlarmContract.Alarms.COLUMN_NAME_ACTIVE, newActiveValue);
        String selection = AlarmContract.Alarms._ID + " = ? ";
        String[] selectionArgs = {alarm.getId() + ""};
        dbWrite.update(AlarmContract.Alarms.TABLE_NAME, values, selection, selectionArgs);
    }

    public void activateAlarm(Alarm alarm){
        changeActivation(alarm, 1);
    }

    public void deactivateAlarm(Alarm alarm){
        changeActivation(alarm, 0);
    }

    public boolean isAlarmActive(int id){
        for (int i = 0; i < getActiveAlarms().size(); i++) {
            if(getActiveAlarms().get(i).getId() == id){
                return true;
            }
        }
        return false;
    }

    public Alarm getAlarm(int id){
        for(int i = 0; i < getAlarms().size(); i++){
            if(getAlarms().get(i).getId() == id){
                return getAlarms().get(i);
            }
        }
        return null;
    }
}
