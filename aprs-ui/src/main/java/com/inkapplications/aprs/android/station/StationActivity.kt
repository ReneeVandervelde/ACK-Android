package com.inkapplications.aprs.android.station

import android.app.Activity
import android.os.Bundle
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.setVisibility
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.trackNavigation
import com.inkapplications.kotlin.collectOn
import kimchi.Kimchi
import kotlinx.android.synthetic.main.station.*

private const val EXTRA_ID = "aprs.station.extra.id"

class StationActivity: ExtendedActivity() {
    private lateinit var stationEvents: StationEvents

    private val id get() = intent.getLongExtra(EXTRA_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station)

        stationEvents = component.stationEvents()
    }

    override fun onStart() {
        super.onStart()

        station_map.getMap(this) { map ->
            stationEvents.stateEvents(id).collectOn(foregroundScope) { viewModel ->
                map.zoomTo(viewModel.center, viewModel.zoom)
                map.showMarkers(viewModel.markers)
                station_map.setVisibility(viewModel.mapVisible)
                station_name.text = viewModel.name
                station_comment.text = viewModel.comment
                station_temperature.setVisibility(viewModel.temperatureVisible)
                station_temperature.text = viewModel.temperature
                station_wind.setVisibility(viewModel.windVisible)
                station_wind.text = viewModel.wind
            }
        }
    }
}

fun Activity.startStationActivity(stationId: Long) {
    Kimchi.trackNavigation("station")
    startActivity(StationActivity::class) {
        putExtra(EXTRA_ID, stationId)
    }
}
