package com.draco.nom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsPreferenceFragment : PreferenceFragmentCompat() {
    /* Setup our preference screen */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey)
    }

    /* Process preference clicks */
    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) when (preference.key) {
            /* Take user to the Google Play details page */
            "viewStore" -> {
                val uri = Uri.parse("market://details?id=" + requireContext().packageName)
                val intent = Intent(Intent.ACTION_VIEW, uri)

                startActivity(intent)
            }

            /* Take user to the source code */
            "github" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://github.com/tytydraco/NOM"))

                startActivity(intent)
            }

            /* Send the developer an email */
            "contact" -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:tylernij@gmail.com?subject=NOM%20Feedback"))

                startActivity(intent)
            }

            /* Show open source licenses */
            "licenses" -> {
                startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            }

            /* If we couldn't handle a preference click */
            else -> return super.onPreferenceTreeClick(preference)
        }

        return true
    }
}