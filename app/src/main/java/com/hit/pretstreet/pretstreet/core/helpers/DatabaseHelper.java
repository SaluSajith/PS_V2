package com.hit.pretstreet.pretstreet.core.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hit.pretstreet.pretstreet.navigation.models.TrendingItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by user on 13/7/2017
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;
    private SQLiteDatabase db;
    private ContentValues values;
    private static final String DATABASE_NAME = "pretstreet_db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_RECENT_SEARCH_STORE = "RECENT_SEARCH_STORE";
    ArrayList<HashMap<String, String>> search_list = new ArrayList<>();
    private static final String KEY_STORE_ID = "STORE_ID";
    private static final String KEY_STORE_NAME = "STORE_NAME";
    private static final String KEY_STORE_ADDRESS = "STORE_ADDRESS";

    private static final String TABLE_NOTIFICATION = "NOTIFICATION";
    ArrayList<TrendingItems> notif_list = new ArrayList<>();
    private static final String KEY_BASEID = "NOTIF_BASEID";
    private static final String KEY_ID = "NOTIF_ID";
    private static final String KEY_TITLE = "NOTIF_TITLE";
    private static final String KEY_DESC = "NOTIF_DESC";
    private static final String KEY_IMAGE = "NOTIF_IMAGE";
    private static final String KEY_SHARE = "NOTIF_SHARE";
    private static final String KEY_ICON = "NOTIF_ICON";
    private static final String KEY_SEENFLAG = "NOTIF_SEENFLAG";

    private static final String TABLE_RECENT_SEARCH_LOCATION = "RECENT_SEARCH_LOCATION";
    private static final String KEY_LOCATION_NAME = "LOCATION_NAME";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STORE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_RECENT_SEARCH_STORE +
                " (" + KEY_STORE_ID + " VARCHAR, "
                + KEY_STORE_NAME + " VARCHAR, "
                + KEY_STORE_ADDRESS + " VARCHAR);";

        String CREATE_NOTIF_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION +
                " (" + KEY_TITLE + " VARCHAR, "
                + KEY_BASEID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ID + " VARCHAR, "
                + KEY_DESC + " VARCHAR, "
                + KEY_IMAGE + " VARCHAR, "
                + KEY_SHARE + " VARCHAR, "
                + KEY_SEENFLAG + " VARCHAR, "
                + KEY_ICON + " VARCHAR);";

        String CREATE_LOCATION_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_RECENT_SEARCH_LOCATION +
                " (" + KEY_LOCATION_NAME + " VARCHAR)";

        db.execSQL(CREATE_STORE_TABLE);
        db.execSQL(CREATE_NOTIF_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCH_STORE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCH_LOCATION);
            onCreate(db);
        }
    }

    public long saveNotif(TrendingItems trendingItems) {
        long l = 0;
        try {
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NOTIFICATION, null);
            if(c.getCount()>=5){
                deleteLastRowNotification();
            }
            //db.execSQL("delete from "+ TABLE_NOTIFICATION);
            //if(!CheckIsDataAlreadyInDBorNot(TABLE_NOTIFICATION, KEY_ID, trendingItems.getId())) {
                values = new ContentValues();
                values.put(KEY_ID, trendingItems.getId());
                values.put(KEY_TITLE, trendingItems.getTitle());
                values.put(KEY_DESC, trendingItems.getArticle());
                values.put(KEY_IMAGE, (trendingItems.getImagearray().size() > 0 ? trendingItems.getImagearray().get(0) : ""));
                values.put(KEY_SHARE, trendingItems.getShareUrl());
                values.put(KEY_ICON, trendingItems.getLogoImage());
                l = db.insert(TABLE_NOTIFICATION, null, values);
            //}
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                               String dbfield, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public long saveSearches(String sid, String sname, String address) {
        db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT  * FROM RECENT_SEARCH_STORE where STORE_ID = " + sid , null);
        int i = c.getCount();
        if(i!=0) {
            while (c.moveToNext()) {
                String rowId = c.getString(c.getColumnIndex(KEY_STORE_ID));
                db.delete(TABLE_RECENT_SEARCH_STORE, KEY_STORE_ID + "=?", new String[]{rowId});
            }
        }
        values = new ContentValues();
        values.put(KEY_STORE_ID, sid);
        values.put(KEY_STORE_NAME, sname);
        values.put(KEY_STORE_ADDRESS, address);
        long l = db.insert(TABLE_RECENT_SEARCH_STORE, null, values);
        db.close();
        return l;
    }

    public ArrayList<TrendingItems> fetchNotifList() {
        notif_list.clear();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NOTIFICATION, null);
        /*if(c.getCount()>=2){
            deleteLastSavedNotifRow();
        }
        */while (c.moveToNext()) {
            TrendingItems trendingItems = new TrendingItems();
            trendingItems.setId(c.getString(c.getColumnIndex(KEY_ID)));
            trendingItems.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            trendingItems.setArticle(c.getString(c.getColumnIndex(KEY_DESC)));
            ArrayList imgArray = new ArrayList();
            imgArray.add(c.getString(c.getColumnIndex(KEY_IMAGE)));
            trendingItems.setImagearray(imgArray);
            trendingItems.setShareUrl(c.getString(c.getColumnIndex(KEY_SHARE)));
            trendingItems.setLogoImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
            trendingItems.setNotifPage(true);
            notif_list.add(trendingItems);
        }
        Collections.reverse(notif_list);
        db.close();
        return notif_list;
    }

    public ArrayList<HashMap<String, String>> fetchSearchList() {
        search_list.clear();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM RECENT_SEARCH_STORE ", null);
        while (c.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", c.getString(c.getColumnIndex(KEY_STORE_ID)));
            hashMap.put("name", c.getString(c.getColumnIndex(KEY_STORE_NAME)));
            hashMap.put("address", c.getString(c.getColumnIndex(KEY_STORE_ADDRESS)));
            search_list.add(hashMap);
        }
        db.close();
        return search_list;
    }

    public int fetchPlacesCount() {
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM RECENT_SEARCH_STORE", null);
        int i = c.getCount();
        db.close();
        return i;
    }

    public void deleteLastRow() {
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_SEARCH_STORE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_STORE_ID));
            db.delete(TABLE_RECENT_SEARCH_STORE, KEY_STORE_ID + "=?", new String[]{rowId});
        }
        db.close();
    }

    public long saveLocation(String loc) {
        db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT  * FROM RECENT_SEARCH_LOCATION where LOCATION_NAME = '" + loc +"'", null);
        int i = c.getCount();
        if(i!=0) {
            while (c.moveToNext()) {
                String rowId = c.getString(c.getColumnIndex(KEY_LOCATION_NAME));
                db.delete(TABLE_RECENT_SEARCH_LOCATION, KEY_LOCATION_NAME + "=?", new String[]{rowId});
            }
        }

        values = new ContentValues();
        values.put(KEY_LOCATION_NAME, loc);
        long l = db.insert(TABLE_RECENT_SEARCH_LOCATION, null, values);
        db.close();
        return l;
    }

    public ArrayList<HashMap<String, String>> fetchLocationList() {
        search_list.clear();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  * FROM RECENT_SEARCH_LOCATION ", null);
        while (c.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", c.getString(c.getColumnIndex(KEY_LOCATION_NAME)));
            search_list.add(hashMap);
        }
        db.close();
        return search_list;
    }

    public int fetchLocationCount() {
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM RECENT_SEARCH_LOCATION", null);
        int i = c.getCount();
        db.close();
        return i;
    }

    public void deleteFirstSavedLocationRow() {
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_SEARCH_LOCATION, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_LOCATION_NAME));
            db.delete(TABLE_RECENT_SEARCH_LOCATION, KEY_LOCATION_NAME + "=?", new String[]{rowId});
        }
        db.close();
    }

    public void deleteLastSavedNotifRow() {
        try {
            db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_NOTIFICATION, null, null, null, null, null, null);
            // if (cursor.moveToFirst()) {
            System.out.println("Notif deleteLastSavedNotifRow "  +" "+cursor.getCount());
            //String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));
            //db.delete(TABLE_NOTIFICATION, KEY_ID + "=?", new String[]{rowId});
            db.rawQuery("DELETE FROM "+TABLE_NOTIFICATION+" WHERE "+KEY_BASEID+" = (SELECT MAX("+KEY_BASEID+") FROM "+TABLE_NOTIFICATION+")", null);

            //}
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLastRowNotification() {
        //db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATION, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_BASEID));
            db.delete(TABLE_NOTIFICATION, KEY_BASEID + "=?", new String[]{rowId});
        }
        //db.close();
    }

    public int getNotifCount() {
        int count = 0;
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATION, null, null, null, null, null, null);
        count = cursor.getCount();
        db.close();
        return count;
    }

}
