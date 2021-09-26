package com.test.rnids.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import android.widget.Button
import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.test.rnids.MainActivity
import com.test.rnids.Support


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.test.rnids.R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(com.test.rnids.R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val supportButton = findViewById<Button>(com.test.rnids.R.id.supportButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, Support::class.java)
            startActivity(intent)
        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            setPreferencesFromResource(com.test.rnids.R.xml.root_preferences, rootKey)


        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}