package com.github.kr328.link.qq;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class MainFragment extends PreferenceFragmentCompat {
    private final int REQUEST_CODE_PERMISSION = 100;
    private final String RUNTIME_PACKAGE_NAME = "com.github.kr328.intent";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("options");

        setPreferencesFromResource(R.xml.main, rootKey);

        Preference status = getPreferenceScreen().findPreference("status");

        boolean installed;

        try {
            requireActivity().getPackageManager().getApplicationInfo(RUNTIME_PACKAGE_NAME, 0);

            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }

        if (!installed) {
            status.setSummary(R.string.runtime_not_found);

            return;
        }

        boolean granted = requireActivity()
                .checkSelfPermission(Constants.INTENT_INTERCEPTOR_PERMISSION) == PackageManager.PERMISSION_GRANTED;

        if (granted) {
            status.setSummary(R.string.authorized);
        } else {
            status.setSummary(R.string.unauthorized);
        }

        status.setOnPreferenceClickListener((Preference preference) -> {
            requestPermissions(new String[]{Constants.INTENT_INTERCEPTOR_PERMISSION}, REQUEST_CODE_PERMISSION);

            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Preference status = getPreferenceScreen().findPreference("status");

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                status.setSummary(R.string.authorized);
            } else {
                status.setSummary(R.string.unauthorized);
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
