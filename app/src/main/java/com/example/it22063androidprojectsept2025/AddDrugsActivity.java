package com.example.it22063androidprojectsept2025;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddDrugsActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText nameInput, descInput, doctorNameInput, doctorLocInput;
    private TextView startDateInput, endDateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge UI layout
        setContentView(R.layout.activity_add_drugs);

        // Link views with their IDs
        nameInput = findViewById(R.id.shortName);
        descInput = findViewById(R.id.desc);
        spinner = findViewById(R.id.spinner);
        startDateInput = findViewById(R.id.startDate);
        endDateInput = findViewById(R.id.endDate);
        doctorNameInput = findViewById(R.id.doctorName);
        doctorLocInput = findViewById(R.id.editTextTextPostalAddress);

        // Load drugs and time terms from the database (in background thread)
        Executors.newSingleThreadExecutor().execute(() -> {
            DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());

            // Log all existing drugs in the database
            List<Drug> drugs = db.drugDao().getAllDrugsNow(); // synchronous call
            Log.d("DB_LOG", "=== Drugs in DB ===");
            for (Drug d : drugs) {
                Log.d("DB_LOG", d.shortName + " | " + d.briefDesc + " | start: " + d.startDate + " | end: " + d.endDate);
            }

            // Log all time terms in the database
            List<TimeTerm> terms = db.timeTermDao().getAll();
            Log.d("DB_LOG", "=== TimeTerms in DB ===");
            for (TimeTerm t : terms) {
                Log.d("DB_LOG", t.id + " | " + t.label);
            }
        });

        // Set up the spinner with predefined time term labels
        List<String> labels = Arrays.asList(
                "before-breakfast", "at-breakfast", "after-breakfast",
                "before-lunch", "at-lunch", "after-lunch",
                "before-dinner", "at-dinner", "after-dinner"
        );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Adjust view padding based on system window insets (status bar, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Opens a date picker dialog and sets selected date in the TextView
    public void showDatePickerDialog(View v){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(calendar.getTime());
                    ((TextView) v).setText(formattedDate); // Set selected date to the clicked TextView
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Called when "Add Drug" button is clicked
    public void AddDrug(View v) throws ParseException {
        // Get user input values
        String drugName = nameInput.getText().toString().trim();
        String briefDesc = descInput.getText().toString().trim();
        String timeTermLabel = spinner.getSelectedItem().toString();
        String startDateTxt = startDateInput.getText().toString();
        String endDateTxt = endDateInput.getText().toString();
        String doctorName = doctorNameInput.getText().toString();
        String doctorLoc = doctorLocInput.getText().toString();

        // Validate required fields (Dr. related values can be null)
        if (drugName.isEmpty() || drugName.equals("Drug's Name") ||
                briefDesc.isEmpty() || briefDesc.equals("Brief Description") ||
                startDateTxt.equals("Start Date") || endDateTxt.equals("End Date")) {

            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date startDate = sdf.parse(startDateTxt);
        Date endDate = sdf.parse(endDateTxt);

        // Validate date logic
        if (endDate.before(startDate)){
            Toast.makeText(this, "End Date can't be before Start Date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Treat default doctor's name as empty
        if (doctorName.equals("Doctor's Name")){
            doctorName = null;
        }

        // Save drug to database (background thread)
        String finalDoctorName = doctorName;
        Executors.newSingleThreadExecutor().execute(() -> {
            DrugDatabase db = DrugDatabase.getDatabase(getApplicationContext());
            timeTermDao timeDao = db.timeTermDao();
            int timeTermId = timeDao.getId(timeTermLabel); // Get ID for the selected time term label

            // Create and insert the Drug object
            Drug newDrug = new Drug(
                    drugName,
                    briefDesc,
                    timeTermId,
                    startDate,
                    endDate,
                    finalDoctorName,
                    doctorLoc,
                    null,      // lastDateReceived
                    false      // hasReceivedToday
            );
            db.drugDao().insertAll(newDrug);

            // Notify success and close activity (on UI thread)
            runOnUiThread(() -> {
                Toast.makeText(this, "Drug added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainActivity
            });
        });
    }
}
