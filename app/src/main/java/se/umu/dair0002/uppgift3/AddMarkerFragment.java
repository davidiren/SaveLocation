package se.umu.dair0002.uppgift3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.model.LatLng;

import se.umu.dair0002.uppgift3.databinding.FragmentAddMarkerBinding;

public class AddMarkerFragment extends Fragment {

    private FragmentAddMarkerBinding binding;
    private LatLng markerLocation;

    public AddMarkerFragment(){
        //needed empty constructor
    }

    public AddMarkerFragment newInstance(){
        AddMarkerFragment fragment = new AddMarkerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_marker_menu, menu);
    }

    /**
     * gets the user input and saves a marker to the database using a viewmodel
     * @param item - menu button pressed
     * @return - boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        //Handle saving of marker
        switch (item.getItemId()) {
            case R.id.save_marker:
                MarkerViewModel vm = new ViewModelProvider(requireActivity()).get(MarkerViewModel.class);
                vm.addMarker(
                        new Marker(
                                binding.markerTitle.getText().toString(),
                                binding.markerDescription.getText().toString(),
                                markerLocation.longitude,
                                markerLocation.latitude
                        )
                );
                NavHostFragment.findNavController(this).popBackStack(); //go back in stack
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(getArguments() != null){
            markerLocation = AddMarkerFragmentArgs.fromBundle(getArguments()).getMarkerLocation();
        }

        binding = FragmentAddMarkerBinding.inflate(inflater);
        return binding.getRoot();
    }
}
