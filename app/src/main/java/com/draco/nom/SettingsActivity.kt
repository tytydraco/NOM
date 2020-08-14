package com.draco.nom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
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