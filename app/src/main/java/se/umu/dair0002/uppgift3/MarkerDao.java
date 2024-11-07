package se.umu.dair0002.uppgift3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
interface MarkerDao {
    @Query("SELECT * FROM marker order by id DESC")
    LiveData<List<Marker>> getAll();

    @Query("SELECT * from marker where id = :param")
    LiveData<Marker> get(int param);

    @Insert
    void insert(Marker r);

    @Delete
    void delete(Marker r);
}
