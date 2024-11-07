package se.umu.dair0002.uppgift3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter to display all saved markers in a RecyclerView
 */
public class MarkerRecyclerViewAdapter extends RecyclerView.Adapter<MarkerRecyclerViewAdapter.ViewHolder> {

    private final List<Marker> mMarkers;

    public MarkerRecyclerViewAdapter(List<Marker> items){
        mMarkers = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_marker_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = mMarkers.get(position);
        holder.mIdView.setText(mMarkers.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return mMarkers.size();
    }

    /**
     * handle the interaction with the recyclerview
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Marker mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            view.setOnClickListener(e->{
                MarkerFragmentDirections.ActionMarkerFragmentToMarkerDetailsFragment action = MarkerFragmentDirections.actionMarkerFragmentToMarkerDetailsFragment(mItem.getId());
                Navigation.findNavController(mView).navigate(action);
            } );
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
