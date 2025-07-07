package com.example.it22063androidprojectsept2025;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoreInfoActivity extends AppCompatActivity {

    private DrugDetailAdapter adapter;
    private ExecutorService executor;
    private Handler mainHandler;
    private DrugDao dao;
    private timeTermDao timeTermDao;
    private DrugViewmodel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);

        // Initialize executor and handler
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Edge to edge support for padding system bars
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get drug uid from intent
        int drug_uid = getIntent().getIntExtra("drug_uid", -1);

        // Get DAO and ViewModel
        dao = DrugDatabase.getDatabase(getApplicationContext()).drugDao();
        timeTermDao = DrugDatabase.getDatabase(getApplicationContext()).timeTermDao();
        viewModel = new ViewModelProvider(this).get(DrugViewmodel.class);

        // Setup RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.drugDetailRecyclerView);
        adapter = new DrugDetailAdapter(this, dao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Observe the drug by id
        viewModel.getDrugById(drug_uid).observe(this, drug -> {
            if (drug != null) {
                executor.execute(() -> {
                    // Fetch the TimeTerm using foreign key
                    TimeTerm timeTerm = timeTermDao.getTimeTermById(drug.timeTermId);
                    if (timeTerm != null) {
                        drug.timeTermName = timeTerm.label;
                    } else {
                        drug.timeTermName = "Unknown Term";
                    }

                    mainHandler.post(() -> {
                        // Update header text on UI thread
                        TextView headerTextView = findViewById(R.id.drugHeaderTextView);
                        headerTextView.setText(drug.shortName);

                        // Pass updated drug to adapter
                        List<Drug> list = new ArrayList<>();
                        list.add(drug);
                        adapter.setDetails(list);
                    });
                });
            }
        });
    }

    public void deleteDrug(View v) {
        int drug_uid = getIntent().getIntExtra("drug_uid", -1);
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this drug?")
                .setPositiveButton("Continue", (dialog, which) -> {
                    executor.execute(() -> {
                        dao.deleteById(drug_uid);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Drug with UID " + drug_uid + " deleted", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MoreInfoActivity.this, MainActivity.class));
                            finish();
                        });
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void seeDocLocation(View v) {
        startActivity(new Intent(MoreInfoActivity.this, DocLocationActivity.class));
    }
}
