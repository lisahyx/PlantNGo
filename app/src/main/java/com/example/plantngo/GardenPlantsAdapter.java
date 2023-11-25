package com.example.plantngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GardenPlantsAdapter extends RecyclerView.Adapter<GardenPlantsAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<Plant> plants;

    public GardenPlantsAdapter(Context context, List<Plant> plants){
        this.inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.plants = plants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.garden_plants,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(plants.get(position));
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView plantName, plantGrowth;
        ImageView plantImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            plantImage = itemView.findViewById(R.id.plant_image);
            plantName = itemView.findViewById(R.id.plant_name);
            plantGrowth = itemView.findViewById(R.id.plant_growth);
        }
        void bind(final Plant plant) {
            //plantImage.setImageResource(plant.plantImageUrl);
            plantName.setText(plant.plantName);
            plantGrowth.setText(plant.plantGrowth);
        }
    }
}
