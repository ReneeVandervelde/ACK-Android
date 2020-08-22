package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.ApplicationInitializer
import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.aprs.android.firebase.FirebaseModule
import com.inkapplications.aprs.android.log.LogDataAccess
import com.inkapplications.aprs.android.map.MapManagerFactory
import com.inkapplications.aprs.android.map.MapModule
import com.inkapplications.aprs.android.settings.SettingsModule
import com.inkapplications.aprs.android.settings.SettingsAccess
import com.inkapplications.aprs.data.AndroidAprsModule
import com.inkapplications.aprs.data.AprsAccess
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidAprsModule::class,
        ApplicationModule::class,
        ExternalModule::class,
        FirebaseModule::class,
        MapModule::class,
        SettingsModule::class
    ]
)
interface ApplicationComponent {
    fun aprs(): AprsAccess
    fun mapManager(): MapManagerFactory
    fun settingsRepository(): SettingsAccess
    fun logData(): LogDataAccess
    fun initializers(): @JvmSuppressWildcards Set<ApplicationInitializer>
}
