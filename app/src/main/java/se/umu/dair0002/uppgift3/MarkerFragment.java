package se.umu.dair0002.uppgift3;

import android.content.ClipData;
import android.content.Context;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import se.umu.dair0002.uppgift3.databinding.FragmentItemMarkerListBinding;

public class MarkerFragment extends Fragment {

    private MarkerViewModel viewModel;
    private FragmentItemMarkerListBinding binding;

    /**
     * empty constructor
     */
    public MarkerFragment(){

    }


    /**
     * optionsmenu for consistency througout the app
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle item selected in the marker list
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.addMarkerFragment:
                NavHostFragment.findNavController(this).navigate(R.id.action_markerFragment_to_markerDetailsFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static MarkerFragment newInstance(){
        MarkerFragment fragment = new MarkerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(requireActivity()).get(MarkerViewModel.class);//getActivity() changed to requireActivity() to avoid null pointers
    }

    /**
     * Create the view and setup the delete interaction with the list
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentItemMarkerListBinding.inflate(inflater);

        Context context = binding.markerlist.getContext();
        binding.markerlist.setLayoutManager(new LinearLayoutManager(context));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        viewModel.deleteMarker(((MarkerRecyclerViewAdapter.ViewHolder)viewHolder).mItem);
                    }
                });

        itemTouchHelper.attachToRecyclerView(binding.markerlist);
        viewModel.getMarkers().observe(getViewLifecycleOwner(), e ->
                binding.markerlist.setAdapter(new MarkerRecyclerViewAdapter(e)));

        return binding.getRoot();
    }
}
