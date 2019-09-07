package com.hlnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBOpenHelper_a extends SQLiteOpenHelper {
    private String account_creator="create table account_table(create_datetime datetime primary key," +
            "Datetime datetime,account_title,profit,account_class default '未分类')";
    //账本信息表(创建事件,事件日期,标题,盈利,分类)

    public DBOpenHelper_a(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(account_creator);
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
