package se.umu.dair0002.uppgift3;

import androidx.room.RoomDatabase;
/**
 * room database for markers
 */
@androidx.room.Database(entities = {Marker.class},version =1, exportSchema = false)
public abstract class Database extends RoomDatabase{
    abstract MarkerDao markerDao();
}
