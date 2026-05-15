package com.nammaplatform.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaplatform.app.databinding.ActivityMainBinding
import com.nammaplatform.app.ui.home.HomeViewModel
import com.nammaplatform.app.ui.home.StationAdapter

/**
 * Entry screen — passenger picks their station.
 * Big, high-contrast list tiles for elderly / low-vision users.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val adapter = StationAdapter { station ->
            // Pass selected station id forward — keep the Intent extras minimal.
            val i = Intent(this, PlatformActivity::class.java)
                .putExtra(PlatformActivity.EXTRA_STATION_ID, station.id)
            startActivity(i)
        }

        b.rvStations.layoutManager = LinearLayoutManager(this)
        b.rvStations.adapter = adapter

        vm.stations.observe(this) { adapter.submitList(it) }
        vm.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }
    }
}
