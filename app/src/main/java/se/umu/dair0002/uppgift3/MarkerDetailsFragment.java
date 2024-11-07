package se.umu.dair0002.uppgift3;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import se.umu.dair0002.uppgift3.databinding.FragmentMarkerDetailsBinding;

public class MarkerDetailsFragment extends Fragment implements OnMapReadyCallback {

    private MarkerViewModel viewModel;
    private FragmentMarkerDetailsBinding binding;

    private MapView mapView; //mapView in fragment
    private GoogleMap mMap; //map to display saved position

    private LatLng markerPos;
    private Marker savedMarker;

    private final int DEFAULT_ZOOM = 15;
    private static final String SAVED_MARKER = "saved_marker";

    public MarkerDetailsFragment() {
        //required empty constructor
    }

    public static MarkerDetailsFragment newInstance(){
        MarkerDetailsFragment fragment = new MarkerDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        if(savedInstance != null){
            savedMarker = savedInstance.getParcelable(SAVED_MARKER);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(MarkerViewModel.class);//getActivity() changed to requireActivity() to avoid null pointers
        setHasOptionsMenu(true);
    }

    /**
     * setup the app-bar menu
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update_map_menu, menu);
        if(savedMarker != null){
            menu.getItem(0).setEnabled(false);//disable "show marker" to
        }
    }

    /**
     * lets you manually update so that you see marker on map
     * @param item - MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.update_map_menu:
                moveMapCam();
                item.setEnabled(false); //showing marker turn off button
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * create the view, fetch data from database to fill fields in view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int markerId = MarkerDetailsFragmentArgs.fromBundle(getArguments()).getMarkerId();
        LiveData<Marker> markerLiveData = viewModel.getMarker(markerId);

        binding = FragmentMarkerDetailsBinding.inflate(inflater);

        markerLiveData.observe(this.getViewLifecycleOwner(), marker -> {
            binding.title.setText(marker.getTitle());
            binding.description.setText(marker.getDescription());
            binding.longitude.setText(String.valueOf(Math.floor(marker.getLongitude()*1000+0.5)/1000));//round lat and lng
            binding.latitude.setText(String.valueOf(Math.floor(marker.getLatitude()*1000+0.5)/1000));
        });

        //Initialize map
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        return binding.getRoot();
    }

    /**
     * called when map is ready
     * @param googleMap map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);// turn off direction buttons
        if(savedMarker!=null){
            retainCamPos();
        }
    }

    /**
     * do avoid animating the camera movement on rotation
     */
    private void retainCamPos() {
        MarkerOptions mo = new MarkerOptions().position(new LatLng(savedMarker.getLatitude(),savedMarker.getLongitude()));
        mo.title(savedMarker.getTitle());
        mMap.addMarker(mo);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(savedMarker.getLatitude(),
                        savedMarker.getLongitude()),DEFAULT_ZOOM));
    }

    /**
     * make marker and animate move cam to specified marker
     */
    private void moveMapCam(){
        if(savedMarker == null){
            makeMarker();
        }
        mMap.clear();//do not add multiple markers
        MarkerOptions mo = new MarkerOptions().position(new LatLng(savedMarker.getLatitude(),savedMarker.getLongitude()));
        mo.title(savedMarker.getTitle());
        mMap.addMarker(mo);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mo.getPosition(),DEFAULT_ZOOM);
        mMap.animateCamera(cu);
    }

    /**
     * create marker from binding and the textviews
     */
    private void makeMarker() {
        if (binding != null) {
            savedMarker = new Marker(
                    binding.title.getText().toString(),
                    binding.description.getText().toString(),
                    getDoubleFromTextViews(binding.longitude),
                    getDoubleFromTextViews(binding.latitude));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (savedMarker != null){
            outState.putParcelable(SAVED_MARKER, savedMarker);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * will only be called on latitude and longitude textviews which
     * always are doubles, used to get cameralocation for map
     * @param tv - textview
     * @return double
     */
    private double getDoubleFromTextViews(TextView tv) {
        return Double.parseDouble(tv.getText().toString());
    }

    //Required Overrides to make the mapview work
    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}
