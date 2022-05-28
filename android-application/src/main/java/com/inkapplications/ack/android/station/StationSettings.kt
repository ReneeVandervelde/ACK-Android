package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.ZeroInclusivePositiveIntegerValidator
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.IntSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import javax.inject.Inject

@Reusable
class StationSettings @Inject constructor(
    stringResources: StringResources,
    positiveIntegerValidator: ZeroInclusivePositiveIntegerValidator,
): SettingsProvider {
    private val categoryName = stringResources.getString(R.string.station_settings_category)

    val showDebugData = BooleanSetting(
        key = "station.data.debug",
        name = stringResources.getString(R.string.station_settings_debug),
        categoryName = categoryName,
        defaultValue = false,
        advanced = true,
    )

    val recentStationEvents = IntSetting(
        key = "station.data.recent.limit",
        name = stringResources.getString(R.string.station_settings_recent_limit),
        categoryName = categoryName,
        defaultValue = 10,
        advanced = true,
        validator = positiveIntegerValidator,
    )

    override val settings: List<Setting> = listOf(
        showDebugData, recentStationEvents
    )
}
