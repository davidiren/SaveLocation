package se.umu.dair0002.uppgift3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import se.umu.dair0002.uppgift3.databinding.FragmentMapsBinding;


public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    //private PlacesClient placesClient; // to display places
    private FusedLocationProviderClient fusedLocationProviderClient;

    //might be needed idk... probably not needed
    //private MapsActivity viewModel;
    //private FragmentMapsBinding binding;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-34, 151);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private boolean requestingLocationUpdates;
    private boolean alreadyShownLocation = false;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private LatLng markerLocation; //location of marker from onMapClick
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    //strings for saving
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String MARKER_LOCATION = "marker_location";
    private static final String REQUESTING_LOCATION_KEY = "requesting_location_key";

    public MapsActivity() {
        //required empty constructor
    }

    public static MapsActivity newInstance() {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve info
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            if (savedInstanceState.containsKey(MARKER_LOCATION)){
                markerLocation = savedInstanceState.getParcelable(MARKER_LOCATION);
            }
            if (savedInstanceState.containsKey(REQUESTING_LOCATION_KEY)){
                requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_KEY);
            }
        }
        setHasOptionsMenu(true);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        //create the location request and callback
        locationRequest = createLocationRequest();
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult==null){
                    return;
                }
                updateLocationUI();
                getDeviceLocation();
            }
        };

        getMapAsync(this);
    }

    /**
     * creates an appropriate locationrequest for displaying
     * location in real time
     * @return Location request
     */
    private LocationRequest createLocationRequest() {
        LocationRequest lr = LocationRequest.create();
        lr.setInterval(10000);//every 10 seconds
        lr.setFastestInterval(5000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return lr;
    }

    /**
     * continously update device location, if allowed
     */
    @Override
    public void onResume() {
        super.onResume();
        getLocationPermission();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
        //getLocationPermission();
        //updateLocationUI();
        //getDeviceLocation();
    }

    /**
     * stop location updates
     */
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * stop location updates
     */
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * starts requesting location updates
     */
    @SuppressLint("MissingPermission") //permission is checked
    private void startLocationUpdates() {
        //dont start requesting if no permission
        if(!locationPermissionGranted) return;

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * save params
     * @param outState - bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            if(markerLocation != null){
                outState.putParcelable(MARKER_LOCATION, markerLocation);
            }
            outState.putBoolean(REQUESTING_LOCATION_KEY, requestingLocationUpdates);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets up the save menu.
     * @param menu The options menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mi) {

        mi.inflate(R.menu.clear_map, menu);
        mi.inflate(R.menu.location_menu, menu);
        mi.inflate(R.menu.save_menu, menu);
        //return true;
    }

    /**
     * Handle options menu
     * @param item - menu button pressed
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_option:
                saveMarkerLocation();
                return true;
            case R.id.location_menu:
                savedLocations();
                return true;
            case R.id.clear_marker:
                clearMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Clear map and reset markerLocation
     */
    private void clearMap() {
        if(mMap == null) return;

        markerLocation = null; // there is not marker
        mMap.clear(); // clear map
        getActivity().invalidateOptionsMenu();//update options menu
    }

    /**
     * go to saved location list
     */
    private void savedLocations() {
        NavHostFragment.findNavController(this).navigate(R.id.action_mapsActivity_to_markerFragment);
    }

    /**
     * handle so that you cant save a marker that does not exist and cant clear if there is no marker
     * @param menu options menu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (markerLocation == null){
            menu.getItem(2).setEnabled(false);//index 2 is "save"-button, cant save if there is no marker
            menu.getItem(0).setEnabled(false);//index 0 is "clear"-button, cant clear if there is no marker
        }else{
            menu.getItem(2).setEnabled(true);
            menu.getItem(0).setEnabled(true);
        }
    }

    /**
     * save current marker location, goto AddMarkerFragment and --> pass current marker <--
     * button should be disabled if there is no current marker
     */
    private void saveMarkerLocation() {
        if (markerLocation == null) return;// can not save if there is no marker
        // switch current fragment, goto AddMarkerFragment
        MapsActivityDirections.ActionMapsActivityToAddMarkerFragment action = MapsActivityDirections.actionMapsActivityToAddMarkerFragment(markerLocation);
        NavHostFragment.findNavController(this).navigate(action);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //setup map onclick
        setUpMapOnClick();
        mMap.getUiSettings().setMapToolbarEnabled(false);// turn off direction buttons
        //promt user to give permission
        getLocationPermission();
        //Turn on the My Location layer and the related control on the map
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * setup OnMapClickListener so that you can add markers to map
     */
    private void setUpMapOnClick() {
        if(mMap!=null){
            mMap.setOnMapClickListener(latLng -> {
                mMap.clear();//clear map from marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Lat: " +Math.floor(latLng.latitude*1000+0.5)/1000+
                        " Long: " + Math.floor(latLng.longitude*1000+0.5)/1000);
                markerLocation = markerOptions.getPosition();
                // create and set current marker location
                mMap.addMarker(markerOptions);

                getActivity().invalidateOptionsMenu();//call this to update the optionsmenu

            });
        }
    }

    /**
     * handle the result of the permission request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        requestingLocationUpdates = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    requestingLocationUpdates = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            requestingLocationUpdates = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null && !alreadyShownLocation) {
                                alreadyShownLocation = true;//only need to show location once
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


}