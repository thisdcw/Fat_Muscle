package com.mxsella.fatmuscle.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mxsella.fatmuscle.db.bean.FatRecord;

import java.util.List;

@Dao
public interface FatRecordDao {

    @Query("select * from record")
    List<FatRecord> getAllFatRecord();

    @Insert
    void AddRecord(FatRecord record);
}
