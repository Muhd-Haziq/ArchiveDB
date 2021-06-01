package sg.edu.rp.c346.s19047241.archivedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CHARACTERS = "characters";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME_EN = "en_name";
    private static final String COLUMN_NAME_JP = "jp_name";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_ICON = "icon";
    private static final String COLUMN_COUNTER = "count";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNoteTableSql = "CREATE TABLE " + TABLE_CHARACTERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_EN + " TEXT,"
                + COLUMN_NAME_JP + " TEXT,"
                + COLUMN_RARITY + " TEXT,"
                + COLUMN_ICON + " TEXT,"
                + COLUMN_COUNTER + " INTEGER ) ";
        db.execSQL(createNoteTableSql);

//        // Dummy records to be inserted when the database is created
//        for(int i = 0; i < 4; i++){
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_NAME_EN, "Data number " + i);
//            db.insert(TABLE_CHARACTERS, null, values);
//        }
//
//        Log.i("info", "dummy records");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTERS);
        onCreate(db);
    }

    public long insertCharacter(String enName, String jpName, int rarity, String icon, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_EN, enName);
        values.put(COLUMN_NAME_JP, jpName);
        values.put(COLUMN_RARITY, rarity);
        values.put(COLUMN_ICON, icon);
        values.put(COLUMN_COUNTER, count);
        long result = db.insert(TABLE_CHARACTERS, null, values);
        db.close();
        Log.d("SQL Insert","ID:"+ result); //id returned, shouldn’t be -1
        if (result == -1){
            Log.d("DBHelper", "Insert failed");
        }

        return result;
    }

    public ArrayList<LocalCharacter> getAllCharacters(){
        ArrayList<LocalCharacter> localCharacters = new ArrayList<LocalCharacter>();

        String selectQuery = "SELECT " + COLUMN_ID + ","
                + COLUMN_NAME_EN + ","
                + COLUMN_NAME_JP + ","
                + COLUMN_RARITY + ","
                + COLUMN_ICON + ","
                + COLUMN_COUNTER
                + " FROM " + TABLE_CHARACTERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String enName = cursor.getString(1);
                String jpName = cursor.getString(2);
                int rarity = cursor.getInt(3);
                String icon = cursor.getString(4);
                int count = cursor.getInt(5);
                LocalCharacter localCharacter = new LocalCharacter(id, enName, jpName, rarity, icon,count);
                localCharacters.add(localCharacter);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return localCharacters;
    }

    public int updateCount(LocalCharacter data){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNTER, data.getCount());
        String condition = COLUMN_ID + " = ?";
        String[] args = {String.valueOf(data.get_id())};
        int result = db.update(TABLE_CHARACTERS, values, condition, args);
        db.close();

        return result;
    }


    public int deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_CHARACTERS, null, null);

        db.close();

        return result;
    }

    public int deleteCharacter(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String condition = COLUMN_ID + " = ?";
        String[] args = {String.valueOf(id)};
        int result = db.delete(TABLE_CHARACTERS, condition, args);
        db.close();

        return result;
    }
}

