package com.draco.nom.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.draco.nom.R
import com.draco.nom.fragments.SettingsPreferenceFragment

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /* Replace our container with our settings fragment */
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsActivity, SettingsPreferenceFragment())
            .commit()
    }
}