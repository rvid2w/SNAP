package com.theartofdev.edmodo.cropper.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context ctx;
    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_TAG + " TEXT,"
                + Constants.KEY_IMAGE + " BLOB );";
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    void insetImage(Drawable dbDrawable, int imageId, String imageTag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_ID, imageId);
        Bitmap bitmap = ((BitmapDrawable)dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(Constants.KEY_IMAGE, stream.toByteArray());
        values.put(Constants.KEY_TAG, imageTag);
        db.insert(Constants.TABLE_NAME, null, values);
        db.close();
    }

    ImageHelper getImage(String imageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.query(Constants.TABLE_NAME,
                new String[] {Constants.KEY_ID, Constants.KEY_TAG, Constants.KEY_IMAGE},Constants.KEY_ID
                        +" LIKE '"+imageId+"%'", null, null, null, null);
        ImageHelper imageHelper = new ImageHelper();

        if (cursor2.moveToFirst()) {
            do {
                imageHelper.setImageId(cursor2.getString(1));
                imageHelper.setImageByteArray(cursor2.getBlob(2));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return imageHelper;
    }

    public void deleteRows(String imageID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[] {imageID});
        db.close();
    }

    public int getMaximumID(){
        SQLiteDatabase db = this.getReadableDatabase();
        int data_id = 0;
        Cursor cursor  = db.rawQuery("SELECT MAX("+Constants.KEY_ID+") FROM " + Constants.TABLE_NAME, null);
        if(cursor!=null) {
            cursor.moveToFirst();
            data_id = cursor.getInt(0);
        }
        assert cursor != null;
        cursor.close();
        return data_id;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    List<Data> getDataList(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Data> dataList = new ArrayList<>();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID, Constants.KEY_TAG, Constants.KEY_IMAGE},null, null, null, null, Constants.KEY_ID);
        if(cursor.moveToFirst()){
            do{
                Data data = new Data();
                data.setTag(cursor.getString(cursor.getColumnIndex(Constants.KEY_TAG)));
                data.setImage(cursor.getBlob(cursor.getColumnIndex(Constants.KEY_IMAGE)));
                data.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                dataList.add(data);
            }while (cursor.moveToNext());
        }
        cursor.close();
//        for(Data d: dataList){
//            System.out.println(d.getID() + " " + d.getTag());
//        }
    return dataList;
    }
}
