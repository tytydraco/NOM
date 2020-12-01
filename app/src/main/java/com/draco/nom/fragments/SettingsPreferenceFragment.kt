package com.draco.nom.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.draco.nom.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsPreferenceFragment: PreferenceFragmentCompat() {
    /* Setup our preference screen */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    /* Process preference clicks */
    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) when (preference.key) {
            /* Take user to the source code */
            getString(R.string.pref_source_code) -> {
                val intent = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://github.com/tytydraco/NOM"))

                try {
                    startActivity(intent)
                } catch(_: Exception) {}
            }

            /* Send the developer an email */
            getString(R.string.pref_contact) -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:tylernij@gmail.com?subject=NOM%20Feedback"))

                try {
                    startActivity(intent)
                } catch(_: Exception) {}
            }

            /* Show open source licenses */
            getString(R.string.pref_licenses) -> {
                startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            }

            /* If we couldn't handle a preference click */
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }
}