package us.wayshine.apollo.myweather;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Apollo on 10/16/15.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/us.wayshine.apollo.myweather/Database/";
    private final static String DB_NAME = "city.list.db";
    private final static String DB_PATH = DB_DIR + DB_NAME;

    private SQLiteDatabase myDataBase = null;

    private final Context mContext;

    public MyDatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(!dbExist) {
            try {
                copyDataBase();
            }
            catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            Log.e("SQLite Database", e.toString());

        }

        if(checkDB != null)
            checkDB.close();

        return checkDB != null;
    }

    public void copyDataBase() throws IOException{

        File dir = new File(DB_DIR);

        if(!dir.exists()) {
            Log.i("dirExists", "" + dir.mkdirs());
        }

        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

    }

    public boolean isOpened() {
        return !(myDataBase == null);
    }

    public String[] getCityList(String text) {
        String query = "SELECT * FROM cities WHERE city LIKE '" + text + "%' ORDER BY city LIMIT 5";
        Cursor c = myDataBase.rawQuery(query, null);
        if(c.getCount() <= 0) {
            c.close();
            return null;
        }
        else {
            int cityCount = c.getCount();
            String[] cities = new String[cityCount];
            c.moveToFirst();
            for(int i = 0; i < cityCount - 1; i++) {
                cities[i] = c.getString(2);
                c.moveToNext();
            }
            c.close();
            return cities;
        }
    }

    public Cursor getCityListCursor(String text) {
        try {
            String query = "SELECT * FROM cities WHERE city LIKE '" + text + "%' ORDER BY city LIMIT 5";
            return myDataBase.rawQuery(query, null);
        } catch(SQLiteException e){
            Log.e("SQLite Database", e.toString());
            return null;
        }
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
