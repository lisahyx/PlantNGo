package com.example.plantngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantRecyclerViewAdapter extends RecyclerView.Adapter<PlantRecyclerViewAdapter.PlantViewHolder> {
    private List<PlantCard> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public PlantRecyclerViewAdapter(Context context, List<PlantCard> data) {
        this.data = data;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.plant_card, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PlantViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        CardView cardView;

        PlantViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.plant_card);

        }

        void bind(final PlantCard cat) {
            cardView = itemView.findViewById(R.id.plant_card);
        }
    }
}
