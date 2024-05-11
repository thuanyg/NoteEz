package com.thuanht.noteez.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thuanht.noteez.R;
import com.thuanht.noteez.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the package manager instance
        PackageManager packageManager = getPackageManager();

        try {
            // Get the package information
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            // Retrieve the version information
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            // Use the version information
            binding.tvVersionApp.setText("V" + versionName + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        eventHandler();
    }

    private void eventHandler() {
        binding.tvVersionApp.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        binding.btnAppInformation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_information_url)));
            startActivity(intent);
        });

        binding.btnPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policy_url)));
            startActivity(intent);
        });

        binding.btnAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_us_url)));
            startActivity(intent);
        });

        binding.toolbarSetting.setNavigationOnClickListener(v -> finish());
    }
}