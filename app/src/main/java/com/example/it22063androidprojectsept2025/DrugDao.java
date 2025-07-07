package com.example.it22063androidprojectsept2025;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DrugDao {
    @Insert
    void insertAll(Drug... drug);

    @Query("SELECT Drug.*, TimeTerm.label AS timeTermName FROM Drug LEFT JOIN TimeTerm ON Drug.time_term_id = TimeTerm.id")    LiveData<List<Drug>> getAllDrugs();

    @Query("DELETE FROM drug")
    void deleteAll();

    @Query("DELETE FROM drug WHERE  uid = :id ")
    void deleteById(int id);

    @Query("SELECT * FROM Drug")
    List<Drug> getAllDrugsNow();

    @Query("SELECT * FROM Drug WHERE uid = :id ")
    LiveData<Drug> getDrugByIdLive(int id);
  // === TODO: Replace this hardcoded location with the actual doctor's location ===

        // Add a marker at the location and move the camera there
    @Update
    void updateDrug(Drug drug);

    @Query("SELECT * FROM Drug WHERE is_active = 1 ORDER BY time_term_id ASC")
    LiveData<List<Drug>> getActiveDrugsOrderedByTimeTerm();


    @Query("SELECT * FROM Drug WHERE is_active = 1 ORDER BY time_term_id ASC")
    List<Drug> getActiveDrugsNowOrdered();

}
