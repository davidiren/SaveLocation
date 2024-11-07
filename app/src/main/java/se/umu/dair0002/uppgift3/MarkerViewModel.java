package se.umu.dair0002.uppgift3;

import android.app.Application;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

/**
 * ViewModel to handle interaction with the Room database
 */
public class MarkerViewModel extends AndroidViewModel {

    public LiveData<List<Marker>> items;
    private static Database db;
    private final MarkerDao dao;

    public MarkerViewModel(@NonNull Application application) {
        super(application);
        Database db = getDatabase();
        dao = db.markerDao();
        items = dao.getAll();
    }

    public LiveData<Marker> getMarker(int markerId) {
        return getDatabase().markerDao().get(markerId);
    }

    public void addMarker(Marker marker){
        new Thread(() -> dao.insert(marker)).start();
    }

    public LiveData<List<Marker>> getMarkers() {
        return items;
    }

    public void deleteMarker(Marker marker){
        new Thread(() -> dao.delete(marker)).start();
    }

    private Database getDatabase() {
        if(db == null){
            db = Room.databaseBuilder(getApplication().getApplicationContext(),
                    Database.class, "markerDB").build();
        }
        return db;
    }
}
