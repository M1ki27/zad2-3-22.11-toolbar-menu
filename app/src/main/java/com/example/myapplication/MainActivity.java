package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ZgloszenieUsterki";

    private final ActivityResultLauncher<Intent> summaryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            String number = result.getData()
                                    .getStringExtra(
                                            SummaryActivity.EXTRA_NUMBER
                                    );

                            Toast.makeText(
                                    this,
                                    getString(
                                            R.string.registered,
                                            number
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {
                            Toast.makeText(
                                    this,
                                    R.string.cancelled,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    private Toolbar toolbar;
    private Spinner spCategory;
    private EditText etDescription;
    private EditText etLocation;
    private RadioGroup rgPriority;
    private CheckBox cbBlocking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate");

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    getString(R.string.toolbar_main_title)
            );
        }

        spCategory = findViewById(R.id.spCategory);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        rgPriority = findViewById(R.id.rgPriority);
        cbBlocking = findViewById(R.id.cbBlocking);

        setupCategorySpinner();

        findViewById(R.id.btnNext)
                .setOnClickListener(v -> validateForm());

        findViewById(R.id.btnAbout)
                .setOnClickListener(v -> openAbout());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.categories,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.menu_main,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareReport();

            return true;

        } else if (id == R.id.action_clear) {
            clearForm();

            return true;

        } else if (id == R.id.action_about) {
            openAbout();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validateForm() {
        if (spCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(
                    this,
                    R.string.validation_category,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String description = etDescription
                .getText()
                .toString()
                .trim();

        if (description.isEmpty()) {
            etDescription.setError(
                    getString(R.string.validation_description)
            );

            return;
        }

        String location = etLocation
                .getText()
                .toString()
                .trim();

        if (location.isEmpty()) {
            etLocation.setError(
                    getString(R.string.validation_location)
            );

            return;
        }

        if (rgPriority.getCheckedRadioButtonId() == -1) {
            Toast.makeText(
                    this,
                    R.string.validation_priority,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        openSummary();
    }

    private void openSummary() {
        String category = spCategory
                .getSelectedItem()
                .toString();

        String description = etDescription
                .getText()
                .toString()
                .trim();

        String location = etLocation
                .getText()
                .toString()
                .trim();

        int selectedPriorityId =
                rgPriority.getCheckedRadioButtonId();

        String priority;

        if (selectedPriorityId == R.id.rbLow) {
            priority = getString(R.string.priority_low);

        } else if (selectedPriorityId == R.id.rbMedium) {
            priority = getString(R.string.priority_medium);

        } else {
            priority = getString(R.string.priority_high);
        }

        boolean blocking = cbBlocking.isChecked();

        Intent intent = new Intent(
                this,
                SummaryActivity.class
        );

        intent.putExtra(
                SummaryActivity.EXTRA_CATEGORY,
                category
        );

        intent.putExtra(
                SummaryActivity.EXTRA_DESCRIPTION,
                description
        );

        intent.putExtra(
                SummaryActivity.EXTRA_LOCATION,
                location
        );

        intent.putExtra(
                SummaryActivity.EXTRA_PRIORITY,
                priority
        );

        intent.putExtra(
                SummaryActivity.EXTRA_BLOCKING,
                blocking
        );

        summaryLauncher.launch(intent);
    }

    private void clearForm() {
        spCategory.setSelection(0);
        etDescription.setText("");
        etLocation.setText("");
        rgPriority.clearCheck();
        cbBlocking.setChecked(false);

        Toast.makeText(
                this,
                R.string.form_cleared,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void shareReport() {
        String category = spCategory
                .getSelectedItem()
                .toString();

        String description = etDescription
                .getText()
                .toString()
                .trim();

        String location = etLocation
                .getText()
                .toString()
                .trim();

        String priority = getSelectedPriority();

        String blocking = cbBlocking.isChecked()
                ? getString(R.string.yes)
                : getString(R.string.no);

        String report = getString(
                R.string.share_report_format,
                category,
                location,
                priority,
                description,
                blocking
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, report);

        try {
            startActivity(
                    Intent.createChooser(
                            intent,
                            getString(R.string.share_title)
                    )
            );

        } catch (ActivityNotFoundException exception) {
            Toast.makeText(
                    this,
                    R.string.no_share_app,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String getSelectedPriority() {
        int selectedPriorityId =
                rgPriority.getCheckedRadioButtonId();

        if (selectedPriorityId == R.id.rbLow) {
            return getString(R.string.priority_low);

        } else if (selectedPriorityId == R.id.rbMedium) {
            return getString(R.string.priority_medium);

        } else if (selectedPriorityId == R.id.rbHigh) {
            return getString(R.string.priority_high);
        }

        return getString(R.string.validation_priority);
    }

    private void openAbout() {
        Intent intent = new Intent(
                this,
                AboutActivity.class
        );

        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "MainActivity onDestroy");
    }
}