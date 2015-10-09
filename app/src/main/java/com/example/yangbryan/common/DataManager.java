package com.example.yangbryan.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by yangbryan on 15/10/8.
 */
public class DataManager {
    private  final int BUFFER_SIZE = 500000;
    //保存的数据库的文件名称

    //该工程的包名称
    public static final String PACKAGE_NAME = "com.example.yangbryan.dotaheros";
    //在手机当中存放数据库的位置
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME +"/databases";
    private  Context mContext;

    public DataManager(Context context) {
        mContext = context;
    }

    public  SQLiteDatabase openDatabase(String db_name) {
        String dbfile = DB_PATH + "/" + db_name+".db";
        try {
            //判断数据库文件是否存在，如果不存在直接导入，否则直接打开
            if (!(new File(dbfile).exists())) {
                int sourceId = mContext.getResources().getIdentifier(db_name, "raw", mContext.getPackageName());
                InputStream is = mContext.getResources().openRawResource(sourceId);
                File dir = new File(DB_PATH);
                if(!dir.exists())//判断文件夹是否存在，不存在就新建一个
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }


}