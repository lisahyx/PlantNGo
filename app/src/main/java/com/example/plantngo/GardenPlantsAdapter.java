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

public class GardenPlantsAdapter extends RecyclerView.Adapter<GardenPlantsAdapter.ViewHolder> implements RecyclerViewInterface{
    LayoutInflater inflater;
    Context context;
    List<Plant> plants;

    private final RecyclerViewInterface recyclerViewOnClick;

    public GardenPlantsAdapter(Context context, List<Plant> plants, RecyclerViewInterface recyclerViewOnClick){
        this.inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.plants = plants;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.garden_plants,parent,false);
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
    public void onItemClick(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView plantName, plantGrowth;
        ImageView plantImage;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewOnClick) {
            super(itemView);

            plantImage = itemView.findViewById(R.id.plant_image);
            plantName = itemView.findViewById(R.id.plant_name);
            plantGrowth = itemView.findViewById(R.id.plant_growth);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewOnClick != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewOnClick.onItemClick(pos);
                        }
                    }
                }
            });
        }
        void bind(final Plant plant) {
            //plantImage.setImageResource(plant.plantImageUrl);
            plantName.setText(plant.plantName);
            plantGrowth.setText(plant.plantGrowth);
        }
    }
}
