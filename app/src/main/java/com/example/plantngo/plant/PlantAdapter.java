package com.example.plantngo.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantngo.R;

import java.util.List;

/**
 * Adapter for displaying plants in the garden using RecyclerView.
 */
public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> implements RecyclerViewInterface {

    private final LayoutInflater inflater;
    private final List<Plant> plants;

    private final RecyclerViewInterface recyclerViewOnClick;

    /**
     * Constructor for GardenPlantsAdapter.
     *
     * @param context              The context in which the adapter will be used
     * @param plants               List of Plant objects to display
     * @param recyclerViewOnClick Callback interface for item click events
     */
    public PlantAdapter(Context context, List<Plant> plants, RecyclerViewInterface recyclerViewOnClick) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.plants = plants;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.garden_plants, parent, false);
        return new ViewHolder(view, recyclerViewOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(plants.get(position));
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    @Override
    public void onItemClick(int position, String plantName) {
        // Handle item click events if needed
    }

    /**
     * ViewHolder class for holding the views of each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        ImageView plantImage;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewOnClick) {
            super(itemView);

            plantImage = itemView.findViewById(R.id.plant_image);
            plantName = itemView.findViewById(R.id.plant_name);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                if (recyclerViewOnClick != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        String plantNameText = plantName.getText().toString();
                        recyclerViewOnClick.onItemClick(pos, plantNameText);
                    }
                }
            });
        }

        /**
         * Binds the data of a Plant object to the views in the ViewHolder.
         *
         * @param plant The Plant object to bind
         */
        void bind(final Plant plant) {
            // Set the plant name in the TextView
            plantName.setText(plant.plantName);
            // Set the plant image (if available)
            // plantImage.setImageResource(plant.plantImageUrl);
        }
    }
}
