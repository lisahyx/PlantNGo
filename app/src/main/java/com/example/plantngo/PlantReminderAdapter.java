package com.example.plantngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantReminderAdapter extends RecyclerView.Adapter<PlantReminderAdapter.ViewHolder> implements RecyclerViewInterface {
    LayoutInflater inflater;
    Context context;
    List<Plant> plants;

    private final RecyclerViewInterface recyclerViewOnClick;

    public PlantReminderAdapter(Context context, List<Plant> plants, RecyclerViewInterface recyclerViewOnClick){
        this.inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.plants = plants;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.plant_reminders,parent,false);
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
        TextView plantName, plantReminders;
        ImageView plantImage;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewOnClick) {
            super(itemView);

            plantName = itemView.findViewById(R.id.plant_name);
            plantReminders = itemView.findViewById(R.id.plant_reminders);
            plantImage = itemView.findViewById(R.id.plant_image);

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

            plantImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewOnClick != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewOnClick.onItemClick(pos);
                            Toast.makeText(plantImage.getContext(), "Plant Image Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        void bind(final Plant plant) {
            //plantImage.setImageResource(plant.plantImageUrl);
            plantName.setText(plant.plantName);
        }
    }
}
