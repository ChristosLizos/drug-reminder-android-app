package com.example.it22063androidprojectsept2025;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface timeTermDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertAll(TimeTerm... timeTerms);

    @Query("SELECT * FROM TimeTerm")
    List<TimeTerm> getAll();

    @Query("SELECT id FROM TimeTerm WHERE label = :label LIMIT 1")
     int getId(String label);

    @Query("SELECT * FROM TimeTerm WHERE id = :id LIMIT 1")
    TimeTerm getTimeTermById(int id);

}
