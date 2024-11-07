package se.umu.dair0002.uppgift3;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;


/**
 * marker is a class that will hold a saved marker and its position
 */
@Entity
public class Marker implements Parcelable {

    private String title;
    private String description;
    private double longitude;
    private double latitude;

    @PrimaryKey(autoGenerate = true)
    private int id;

    public Marker(String title, String description, double longitude, double latitude){
        setTitle(title);
        setDescription(description);
        setLongitude(longitude);
        setLatitude(latitude);
    }


    protected Marker(Parcel in) {
        title = in.readString();
        description = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        id = in.readInt();
    }

    //getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public static final Creator<Marker> CREATOR = new Creator<Marker>() {
        @Override
        public Marker createFromParcel(Parcel in) {
            return new Marker(in);
        }

        @Override
        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };
}
