package com.inkapplications.aprs.android.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.capture.log.LogItemState
import com.inkapplications.aprs.android.capture.map.*
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.map.lifecycleObserver
import com.inkapplications.aprs.android.settings.SettingsActivity
import com.inkapplications.aprs.android.station.startStationActivity
import com.inkapplications.aprs.android.trackNavigation
import com.inkapplications.coroutines.collectOn
import com.mapbox.mapboxsdk.maps.MapView
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class CaptureActivity: ExtendedActivity() {
    private val captureScreenState = MutableStateFlow(CaptureScreenState())
    private lateinit var mapEventsFactory: MapEventsFactory
    private var mapView: MapView? = null
    private var map: Map? = null
    private var mapScope: CoroutineScope = MainScope()
    private val mapViewModel = MutableStateFlow(MapViewModel())
    private var recording: Job? = null
    private lateinit var captureEvents: CaptureEvents

    private val locationPermissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("location_permission_grant")
            onLocationEnableClick()
        } else {
            Kimchi.trackEvent("location_permission_deny")
        }
    }

    private val micPermissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("record_permission_grant")
            onRecordingPermissionsGranted()
        } else {
            Kimchi.trackEvent("record_permission_deny")
        }
    }

    override fun onCreate() {
        super.onCreate()
        mapEventsFactory = component.mapManager()
        val logData = component.logData()

        setContent {
            val captureState = captureScreenState.collectAsState()
            val mapState = mapViewModel.collectAsState()
            val logState = logData.logViewModels.collectAsState(emptyList())

            CaptureScreen(
                captureScreenState = captureState,
                mapState = mapState,
                logs = logState,
                mapFactory = ::createMapView,
                onRecordingEnableClick = ::onRecordingEnableClick,
                onRecordingDisableClick = ::onRecordingDisableClick,
                onSettingsClick = ::onSettingsClick,
                onLogClick = ::onLogClick,
                onLocationEnableClick = ::onLocationEnableClick,
                onLocationDisableClick = ::onLocationDisableClick,
            )
        }
        captureEvents = component.captureEvents()
    }

    private fun createMapView(context: Context): View {
        return if(mapView != null) mapView!! else  MapView(context).also { mapView ->
            this.mapView = mapView

            mapView.getMap(this, ::onMapLoaded)
            lifecycle.addObserver(mapView.lifecycleObserver)

            return mapView
        }
    }

    private fun onMapLoaded(map: Map) {
        this.map = map
        mapScope.cancel()
        mapScope = MainScope()
        val manager = mapEventsFactory.createEventsAccess(map)

        manager.viewState.collectOn(mapScope) { state ->
            Kimchi.trackEvent("map_markers", listOf(intProperty("quantity", state.markers.size)))
            mapViewModel.emit(state)
            map.showMarkers(state.markers)
        }
    }


    private fun onLogClick(state: LogItemState) {
        startStationActivity(state.id)
    }

    private fun onLocationEnableClick() {
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> map?.enablePositionTracking()
            else -> locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onLocationDisableClick() {
        map?.disablePositionTracking()
    }
    private fun onRecordingEnableClick() {
        Kimchi.trackEvent("record_enable")
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> onRecordingPermissionsGranted()
            else -> micPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun onRecordingPermissionsGranted() {
        Kimchi.info("Start Recording")
        captureScreenState.value = captureScreenState.value.copy(
            recordingEnabled = true,
        )
        recording = foregroundScope.launch { captureEvents.listenForPackets() }
    }

    private fun onRecordingDisableClick() {
        Kimchi.trackEvent("record_disable")
        captureScreenState.value = captureScreenState.value.copy(
            recordingEnabled = false,
        )
        recording?.cancel()
        recording = null
    }

    private fun onSettingsClick() {
        Kimchi.trackNavigation("settings")
        startActivity(SettingsActivity::class)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
        super.onLowMemory()
    }
}
