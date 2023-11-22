package com.example.plantngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<Plant> plants;

    public Adapter(Context context, List<Plant> plants){
        this.inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.plants = plants;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.plant_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(plants.get(position));
        // bind the data
//        holder.plantName.setText(plants.get(position).getPlantName());
//        holder.plantReminders.setText(plants.get(position).getPlantReminders());
//        Picasso.get().load(plants.get(position).getPlantImageUrl()).into(holder.plantImage);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView plantName, plantReminders;
        ImageView plantImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            plantName = itemView.findViewById(R.id.plant_name);
            plantReminders = itemView.findViewById(R.id.plant_reminders);
            plantImage = itemView.findViewById(R.id.plant_image);
        }
        void bind(final Plant plant) {
            //plantImage.setImageResource(plant.plantImageUrl);
            plantName.setText(plant.plantName);
        }
    }
}
