package com.github.kr328.link.qq

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "options"

        setPreferencesFromResource(R.xml.main, rootKey)

        val status = preferenceScreen.findPreference<Preference>("status")

        try {
            requireActivity().packageManager.getApplicationInfo(RUNTIME_PACKAGE_NAME, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            status!!.setSummary(R.string.runtime_not_found)

            return
        }

        val granted = requireActivity()
            .checkSelfPermission(Constants.INTENT_INTERCEPTOR_PERMISSION) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            status!!.setSummary(R.string.authorized)
        } else {
            status!!.setSummary(R.string.unauthorized)
        }

        status.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requestPermissions(
                    arrayOf(Constants.INTENT_INTERCEPTOR_PERMISSION),
                    REQUEST_CODE_PERMISSION
                )
                true
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            val status = preferenceScreen.findPreference<Preference>("status")

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                status!!.setSummary(R.string.authorized)
            } else {
                status!!.setSummary(R.string.unauthorized)
            }

            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
        private const val RUNTIME_PACKAGE_NAME = "com.github.kr328.intent"
    }
}