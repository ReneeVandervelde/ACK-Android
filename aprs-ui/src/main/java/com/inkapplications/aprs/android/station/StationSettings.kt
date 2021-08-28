package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.Setting
import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Reusable
import javax.inject.Inject

@Reusable
class StationSettings @Inject constructor(): SettingsProvider {
    val showDebugData = BooleanSetting(
        key = "station.data.debug",
        name = "Show Station Debugging Data",
        categoryName = "Station Info",
        defaultValue = false,
        advanced = true,
    )

    override val settings: List<Setting> = listOf(
        showDebugData
    )
}
