package com.example.newgemini;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class CVEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cveditor);

        // Enable edge-to-edge experience
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}