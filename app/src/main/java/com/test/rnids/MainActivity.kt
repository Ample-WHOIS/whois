package com.test.rnids

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.test.rnids.databinding.ActivityMainBinding
import com.test.rnids.providers.TLDProvider
import com.test.rnids.ui.settings.SettingsActivity
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        @JvmStatic lateinit var whoisClientWrapper: WHOISClientWrapper
    }

    lateinit var TLDProviderInst: TLDProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TLDProviderInst = TLDProvider(applicationContext)
        DepResolver.setCommonArg(applicationContext)

        whoisClientWrapper = WHOISClientWrapper(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_lookup, R.id.navigation_results, R.id.navigation_history))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK != UI_MODE_NIGHT_YES)
        {
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(-android.R.attr.state_checked)
            )

            val colors = intArrayOf(
                ContextCompat.getColor(applicationContext, R.color.white),
                ContextCompat.getColor(applicationContext, R.color.primary_400),
                ContextCompat.getColor(applicationContext, R.color.primary_400),
                ContextCompat.getColor(applicationContext, R.color.white)
            )

            binding.navView.itemIconTintList = ColorStateList(states, colors)
            binding.navView.itemTextColor = binding.navView.itemIconTintList
        }

        supportActionBar!!.hide()

        binding.settingsBtn.setOnClickListener { settingsButtonListener() }
    }

    fun switchToResults()
    {
        val results: View = findViewById(R.id.navigation_results)
        results.performClick()
    }

    private fun settingsButtonListener()
    {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}