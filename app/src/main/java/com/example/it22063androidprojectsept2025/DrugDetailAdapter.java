package com.example.it22063androidprojectsept2025;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RecyclerView Adapter for displaying detailed drug information in a list.
 */
public class DrugDetailAdapter extends RecyclerView.Adapter<DrugDetailAdapter.ViewHolder> {

    private List<Drug> details; // List of Drug objects to display
    private final Context context;
    private final DrugDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // For background DB updates

    //used for MoreInfoActivity
    public DrugDetailAdapter(Context context, DrugDao dao) {
        this.context = context;
        this.dao = dao;
    }

    /**
     * Updates the adapter with a new list of drugs.
     */
    public void setDetails(List<Drug> details) {
        this.details = details;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrugDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drug_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugDetailAdapter.ViewHolder holder, int position) {
        Drug drug = details.get(position); // Get current drug

        // Populate basic fields
        holder.uid.setText("UID: " + drug.uid);
        holder.shortName.setText("NAME: " + drug.shortName);
        holder.briefDesc.setText("DESCRIPTION: " + drug.briefDesc);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        // Time term label (already set externally)
        holder.timeTerm.setText("TIME TERM: " + (drug.timeTermName != null ? drug.timeTermName : "Unknown"));

        // Format start and end dates
        holder.startDate.setText("START DATE: " + (drug.startDate != null ? sdf.format(drug.startDate) : "N/A"));
        holder.endDate.setText("END DATE: " + (drug.endDate != null ? sdf.format(drug.endDate) : "N/A"));

        // Doctor name
        holder.doctorName.setText("DOCTOR NAME: " + (drug.docName != null ? drug.docName : "N/A"));

        // Handle doctor location and open in Maps if clicked
        if (drug.docLocation != null && !drug.docLocation.trim().isEmpty()) {
            holder.docLocationTextView.setVisibility(View.VISIBLE);
            holder.docLocationTextView.setText(drug.docLocation);
            holder.docLocationTextView.setClickable(true);

            holder.docLocationTextView.setOnClickListener(v -> {
                String location = drug.docLocation.trim();

                // Try opening Google Maps app
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    // Fallback to browser if Maps not available
                    Uri browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location));
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                    context.startActivity(browserIntent);
                }
            });
        } else {
            // Hide if no location provided
            holder.docLocationTextView.setVisibility(View.GONE);
            holder.docLocationTextView.setClickable(false);
            holder.docLocationTextView.setOnClickListener(null);
        }

        // Active status
        holder.isActive.setText("IS ACTIVE: " + (drug.isActive ? "Yes" : "No"));

        // Checkbox state for "Has received today"
        holder.hasReceivedTodayCheckBox.setChecked(drug.hasReceivedToday);
        holder.hasReceivedTodayCheckBox.setClickable(true);
        holder.hasReceivedTodayCheckBox.setFocusable(true);

        // Handle checkbox toggle: update both UI and DB
        holder.hasReceivedTodayCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Prevent unnecessary writes
            if (drug.hasReceivedToday != isChecked) {
                drug.hasReceivedToday = isChecked;

                // Update the lastDateReceived if checked
                if (isChecked) {
                    drug.lastDateReceived = new Date();
                }

                // Persist changes to database in background
                executor.execute(() -> dao.updateDrug(drug));

                // Refresh item in UI
                notifyItemChanged(position);
            }
        });

        // Display last date drug was taken
        holder.lastDateReceived.setText("LAST DATE RECEIVED: " +
                (drug.lastDateReceived != null ? sdf.format(drug.lastDateReceived) : "N/A"));
    }

    @Override
    public int getItemCount() {
        return details != null ? details.size() : 0;
    }

    /**
     * ViewHolder class to hold references to all the views for a single item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView uid, shortName, briefDesc, timeTerm, startDate, endDate, doctorName,
                docLocationTextView, isActive, lastDateReceived;
        CheckBox hasReceivedTodayCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uid = itemView.findViewById(R.id.uid);
            shortName = itemView.findViewById(R.id.shortName);
            briefDesc = itemView.findViewById(R.id.briefDesc);
            timeTerm = itemView.findViewById(R.id.timeTerm);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            doctorName = itemView.findViewById(R.id.doctorName);
            docLocationTextView = itemView.findViewById(R.id.docLocationTextView);
            isActive = itemView.findViewById(R.id.isActive);
            hasReceivedTodayCheckBox = itemView.findViewById(R.id.hasReceivedTodayCheckBox);
            lastDateReceived = itemView.findViewById(R.id.lastDateReceived);
        }
    }
}
