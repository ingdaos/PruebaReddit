package prueba.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PERSONAL on 23/01/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private String TAG = DBHandler.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reddit.db";
    public static final String DATABASE_TABLE = "INFORMATION";
    public static final String DATABASE_TABLE_ID = "ID";
    public static final String DATABASE_TABLE_NAME = "NAME";
    public static final String DATABASE_TABLE_ICON = "ICON";
    public static final String DATABASE_TABLE_TITLE = "TITLE";
    public static final String DATABASE_TABLE_DESCRIPTION = "DESCRIPTION";
    public static final String DATABASE_TABLE_BANNER = "BANNER";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + DATABASE_TABLE + " (");
        sb.append(DATABASE_TABLE_ID + " INTEGER PRIMARY KEY,");
        sb.append(DATABASE_TABLE_NAME + " TEXT UNIQUE,");
        sb.append(DATABASE_TABLE_ICON + " BLOB,");
        sb.append(DATABASE_TABLE_TITLE + " TEXT NOT NULL,");
        sb.append(DATABASE_TABLE_DESCRIPTION + " TEXT NOT NULL,");
        sb.append(DATABASE_TABLE_BANNER + " BLOB)");
        Log.d(TAG, "onCreate " + sb.toString());

        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(RedditInfoDB row) {
        Log.d(TAG, "Insertando datos");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATABASE_TABLE_NAME, row.getName());
        values.put(DATABASE_TABLE_ICON, getBytes(row.getIcon()));
        values.put(DATABASE_TABLE_TITLE, row.getTitle());
        values.put(DATABASE_TABLE_DESCRIPTION, row.getDescription());
        values.put(DATABASE_TABLE_BANNER, getBytes(row.getBanner()));
        db.insertWithOnConflict(DATABASE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<RedditInfoDB> read() {
        Log.d(TAG, "Leyendo datos");
        String query = "SELECT * FROM INFORMATION";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<RedditInfoDB> data = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                RedditInfoDB tmp = new RedditInfoDB();
                tmp.setId(cursor.getString(0));
                tmp.setName(cursor.getString(1));
                tmp.setIcon(getImage(cursor.getBlob(2)));
                tmp.setTitle(cursor.getString(3));
                tmp.setDescription(cursor.getString(4));
                tmp.setBanner(getImage(cursor.getBlob(5)));
                data.add(tmp);
            } while (cursor.moveToNext());
        }
        db.close();

        return data;
    }

    public Bitmap getBanner(String title) {
        Log.d(TAG, "Obteniendo imagen banner del titulo: " + title);

        Bitmap resp = null;
        String query = "SELECT BANNER FROM INFORMATION WHERE TITLE = '" + title + "'";
        Log.d(TAG, "Obteniendo imagen banner query: " + query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                resp = getImage(cursor.getBlob(0));
            } while (cursor.moveToNext());
        }
        db.close();

        return resp;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
