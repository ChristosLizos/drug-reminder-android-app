package com.example.it22063androidprojectsept2025;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private DrugViewmodel drugViewmodel;
    private RecyclerView recyclerView;
    private DrugAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Perform daily drug status checks
        resetDailyDrugStatuses();

        // Enable edge-to-edge screen drawing
        EdgeToEdge.enable(this);

        // Adjust padding to avoid system bars overlapping content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });

        // Ensure default TimeTerms exist in the database (run on background thread)
        try {
            Future<?> future = Executors.newSingleThreadExecutor().submit((Callable<Void>) () -> {
                DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());
                timeTermDao ttDao = db.timeTermDao();
                if (ttDao.getAll().isEmpty()) {
                    String[] labels = {
                            "before-breakfast", "at-breakfast", "after-breakfast",
                            "before-lunch", "at-lunch", "after-lunch",
                            "before-dinner", "at-dinner", "after-dinner"
                    };
                    TimeTerm[] defaults = new TimeTerm[labels.length];
                    for (int i = 0; i < labels.length; i++) {
                        defaults[i] = new TimeTerm(TimeTerm.getId(labels[i]), labels[i]);
                    }
                    ttDao.InsertAll(defaults);
                }
                return null;
            });
            future.get();
        } catch (Exception e) {
            Log.e("MainActivity", "Failed to ensure TimeTerms", e);
        }

        // Setup RecyclerView with a GridLayout of 1 column
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new DrugAdapter(this);
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel and observe changes to active drugs
        drugViewmodel = new ViewModelProvider(this).get(DrugViewmodel.class);
        drugViewmodel.getActiveDrugsOrderedByTimeTerm().observe(this, drugList -> {
            if (drugList == null || drugList.isEmpty()) {
                Log.d("MainActivity", "No drugs found.");
                return;
            }

            // Load TimeTerm labels for each drug in the background
            Executors.newSingleThreadExecutor().execute(() -> {
                DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());
                timeTermDao dao = db.timeTermDao();
                for (Drug drug : drugList) {
                    TimeTerm tt = dao.getTimeTermById(drug.timeTermId);
                    drug.timeTermName = tt != null ? tt.label : "N/A";
                }

                // Update UI with drug list on main thread
                runOnUiThread(() -> adapter.setDrugList(drugList));
            });
        });
    }

    // Launch the activity to add a new drug
    public void LaunchAddDrug(View v) {
        startActivity(new Intent(this, AddDrugsActivity.class));
    }

    // Export all active drugs to an HTML file in the Downloads folder
    public void exportActiveDrugsToDownloads(View view) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());
                DrugDao drugDao = db.drugDao();
                List<Drug> drugs = drugDao.getActiveDrugsNowOrdered();

                String html = generateHtml(drugs);
                final String filename = "my_active_drugs.html";

                // Handle export depending on Android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Use MediaStore API for Android 10+
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/html");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    Uri fileUri = getContentResolver().insert(collection, values);
                    try (OutputStream os = getContentResolver().openOutputStream(fileUri)) {
                        os.write(html.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    // For older versions, write file directly to Downloads
                    File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File outFile = new File(downloads, filename);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(html.getBytes(StandardCharsets.UTF_8));
                    }
                }

                // Notify user of success on the UI thread
                runOnUiThread(() -> Toast.makeText(
                        this,
                        "Exported to Downloads/" + filename,
                        Toast.LENGTH_LONG
                ).show());

            } catch (Exception e) {
                Log.e("EXPORT", "Error exporting drugs", e);
                // Notify user of failure on the UI thread
                runOnUiThread(() -> Toast.makeText(this, "Export failed", Toast.LENGTH_LONG).show());
            }
        });
    }

    // Generate an HTML representation of the list of active drugs
    private String generateHtml(List<Drug> drugs) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Active Drugs</h1>");

        if (drugs.isEmpty()) {
            html.append("<p>No active drugs found.</p>");
        } else {
            html.append("<ul>");
            for (Drug drug : drugs) {
                html.append("<li>")
                        .append("<strong>").append(drug.shortName).append("</strong><br>")
                        .append("Description: ").append(drug.briefDesc).append("<br>")
                        .append("Time Term: ").append(drug.timeTermId).append("<br>")
                        .append("Start Date: ").append(drug.startDate).append("<br>")
                        .append("End Date: ").append(drug.endDate).append("<br>")
                        .append("Doctor: ").append(drug.docName).append(" @ ").append(drug.docLocation).append("<br>")
                        .append("Last Received: ").append(drug.lastDateReceived).append("<br>")
                        .append("</li><br>");
            }
            html.append("</ul>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Perform daily checks to update each drug's status
    private void resetDailyDrugStatuses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());
                DrugDao dao = db.drugDao();
                List<Drug> allDrugs = dao.getAllDrugsNow();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date today = sdf.parse(sdf.format(new Date()));

                for (Drug drug : allDrugs) {
                    // Reset hasReceivedToday to false if lastDateReceived is not today
                    String todayStr = sdf.format(today);
                    String receivedStr = sdf.format(drug.lastDateReceived);

                    if (!(receivedStr.equals(todayStr))) {
                        drug.hasReceivedToday = false;
                    }

                    // Deactivate the drug if today is after its end date
                    if (drug.endDate != null && !drug.endDate.after(today)) {
                        drug.isActive = false;
                    }

                    // Save changes to the database
                    dao.updateDrug(drug);
                }

                Log.d("DrugDailyCheck", "Daily reset completed." );
            } catch (Exception e) {
                Log.e("DrugDailyCheck", "Failed to reset drug statuses", e);
            }
        });
    }
}
