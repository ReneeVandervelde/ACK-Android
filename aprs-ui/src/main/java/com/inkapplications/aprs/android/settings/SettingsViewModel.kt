package com.inkapplications.aprs.android.settings

data class SettingsViewModel(
    val settingsList: List<SettingsGroup> = emptyList(),
    val callsignText: String? = null,
    val verified: Boolean = false,
)
