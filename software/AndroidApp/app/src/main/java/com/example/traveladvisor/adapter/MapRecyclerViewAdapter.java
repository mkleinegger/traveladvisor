package com.example.traveladvisor.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveladvisor.LocationDetailActivity;
import com.example.traveladvisor.MapFragment;
import com.example.traveladvisor.R;
import com.example.traveladvisor.bll.Location;

import java.util.ArrayList;
import java.util.List;

public class MapRecyclerViewAdapter extends RecyclerView.Adapter<MapRecyclerViewAdapter.MyViewHolder> {

    private List<Location> locations;
    private MapFragment activity;

    public MapRecyclerViewAdapter(MapFragment activity, ArrayList<Location> locations) {

        this.activity = activity;
        this.locations = locations;
    }

    @Override
    public MapRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new MapRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MapRecyclerViewAdapter.MyViewHolder holder, int position) {
        Location location = locations.get(position);

        holder.title.setText(location.getBezeichnung());
        holder.description.setText(location.getBeschreibung());
        holder.branchen.setText(location.getBranchenAsString());
        holder.points.setText(String.valueOf(location.getPunkte()) + " Punkte");
        holder.setClickListener(new MapFragment.ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (activity != null) {
                    Intent myIntent = new Intent(activity.getActivity(), LocationDetailActivity.class);
                    myIntent.putExtra("selectedLocation", location);
                    activity.startActivity(myIntent);
                }
            }
        });

        holder.buttonStartNavigation.setOnClickListener((View v) -> {
            activity.startNavigation();
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    /**
     * ViewHolder for RecyclerView.
     */
    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView description;
        TextView points;
        TextView branchen;
        CardView singleCard;
        Button buttonStartNavigation;

        MapFragment.ItemClickListener clickListener;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textview_title);
            description = view.findViewById(R.id.textview_description);
            points = view.findViewById(R.id.textview_punkte);
            branchen = view.findViewById(R.id.textview_branchen);
            buttonStartNavigation = view.findViewById(R.id.button_start_navigation);
            singleCard = view.findViewById(R.id.single_location_cardview);
            singleCard.setOnClickListener(this);
        }

        void setClickListener(MapFragment.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getLayoutPosition());
        }
    }
}
