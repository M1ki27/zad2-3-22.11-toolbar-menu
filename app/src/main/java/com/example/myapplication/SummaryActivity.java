package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "ZgloszenieUsterki";

    public static final String EXTRA_CATEGORY =
            "com.example.myapplication.CATEGORY";

    public static final String EXTRA_DESCRIPTION =
            "com.example.myapplication.DESCRIPTION";

    public static final String EXTRA_LOCATION =
            "com.example.myapplication.LOCATION";

    public static final String EXTRA_PRIORITY =
            "com.example.myapplication.PRIORITY";

    public static final String EXTRA_BLOCKING =
            "com.example.myapplication.BLOCKING";

    public static final String EXTRA_NUMBER =
            "com.example.myapplication.NUMBER";

    private Toolbar toolbar;
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summary);

        Log.d(TAG, "SummaryActivity onCreate");

        toolbar = findViewById(R.id.toolbarSummary);

        setSupportActionBar(toolbar);

        String category = getIntent()
                .getStringExtra(EXTRA_CATEGORY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    getString(
                            R.string.summary_title_with_category,
                            category
                    )
            );

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        tvSummary = findViewById(R.id.tvSummary);

        tvSummary.setText(buildSummary());

        findViewById(R.id.btnEdit)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnConfirm)
                .setOnClickListener(v -> confirm());
    }

    private String buildSummary() {
        Intent intent = getIntent();

        String category = intent.getStringExtra(
                EXTRA_CATEGORY
        );

        String description = intent.getStringExtra(
                EXTRA_DESCRIPTION
        );

        String location = intent.getStringExtra(
                EXTRA_LOCATION
        );

        String priority = intent.getStringExtra(
                EXTRA_PRIORITY
        );

        boolean blocking = intent.getBooleanExtra(
                EXTRA_BLOCKING,
                false
        );

        String blockingText = blocking
                ? getString(R.string.yes)
                : getString(R.string.no);

        return getString(
                R.string.summary_format,
                category,
                location,
                priority,
                description,
                blockingText
        );
    }

    private void confirm() {
        String number = "UST/"
                + (1000 + new Random().nextInt(9000));

        Intent result = new Intent();

        result.putExtra(
                EXTRA_NUMBER,
                number
        );

        setResult(
                RESULT_OK,
                result
        );

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "SummaryActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "SummaryActivity onDestroy");
    }
}