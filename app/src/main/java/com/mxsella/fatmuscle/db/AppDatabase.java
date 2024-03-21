package com.mxsella.fatmuscle.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mxsella.fatmuscle.db.bean.FatRecord;
import com.mxsella.fatmuscle.db.dao.FatRecordDao;

@Database(entities = {FatRecord.class}, version = 1,exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "fat-muscle";
    private static volatile AppDatabase mInstance;

    /**
     * 单例模式
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return mInstance;
    }


    public abstract FatRecordDao fatRecordDao();
}
