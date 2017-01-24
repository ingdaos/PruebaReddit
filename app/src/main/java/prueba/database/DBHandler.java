package prueba.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

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
        sb.append(DATABASE_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
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
        ContentValues values = new ContentValues();
        values.put(DATABASE_TABLE_NAME, row.getName());
        values.put(DATABASE_TABLE_ICON, getBytes(row.getIcon()));
        values.put(DATABASE_TABLE_TITLE, row.getTitle());
        values.put(DATABASE_TABLE_DESCRIPTION, row.getDescription());
        values.put(DATABASE_TABLE_BANNER, getBytes(row.getBanner()));
        //db.insert(DATABASE_TABLE_NAME, null, values);
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
