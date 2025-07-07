package com.example.it22063androidprojectsept2025;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Adapter for displaying a list of Drug objects in a RecyclerView (Used for Main Activity)
public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {

    private List<Drug> drugList = new ArrayList<>();

    private Context context;

    public DrugAdapter(Context context) {
        this.context = context;
    }

    // Method to update the list of drugs and refresh the RecyclerView
    public void setDrugList(List<Drug> drugs) {
        this.drugList = drugs;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    // Creates new view holders (called when there are no existing views to reuse)
    @Override
    public DrugViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each grid item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new DrugViewHolder(view);
    }

    // Binds data to the views in each grid item
    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        Drug drug = drugList.get(position); // Get the drug at the current position

        // Set the text views with drug data
        holder.uid.setText(String.valueOf(drug.uid));
        holder.name.setText(drug.shortName);
        holder.description.setText(drug.briefDesc);
        holder.timeTerm.setText(drug.timeTermName != null ? drug.timeTermName : "N/A");

        // When the drug is clicked, launch the MoreInfoActivity with the drug's UID
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MoreInfoActivity.class);
            intent.putExtra("drug_uid", drug.uid);
            context.startActivity(intent);
        });
    }

    // Returns the number of drugs in the list
    @Override
    public int getItemCount() {
        return drugList.size();
    }

    // ViewHolder class to hold references to views in each grid item
    public class DrugViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, uid, timeTerm;

        DrugViewHolder(View itemView) {
            super(itemView);
            // Link view objects with their IDs from the layout
            name = itemView.findViewById(R.id.shortName);
            description = itemView.findViewById(R.id.briefDesc);
            uid = itemView.findViewById(R.id.uid);
            timeTerm = itemView.findViewById(R.id.timeTerm);
        }
    }

}
