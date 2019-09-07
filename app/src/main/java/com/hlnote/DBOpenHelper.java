package com.hlnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * 数据库连接类
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private String memo_creator="create table memo_table(create_datetime datetime primary key," +
            "start_datetime datetime,end_datetime datetime,memo_title,memo_content text,memo_classification default '未分类')";
    //备忘信息表(开始日期,结束日期,开始时间,结束时间,标题,内容,分类)

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(memo_creator);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,int newVersion) {
        //upgradeDB(sqLiteDatabase,oldVersion,newVersion);
    }


    //递归更新数据库
    /*private void upgradeDB(SQLiteDatabase db,int oldVersion,int newVersion){

        if(oldVersion == 1) {
        }else if(oldVersion == 2) {
        }else if(oldVersion == 3) {
        }else if(oldVersion == 4) {
        }
        //db.execSQL后等于是oldVersion升一级
        oldVersion++;
        //升级后还是比最新版低级的话，继续升级
        if (oldVersion < newVersion) {
            //递归
            upgradeDB(db,oldVersion,newVersion);
        }
    }*/
}
