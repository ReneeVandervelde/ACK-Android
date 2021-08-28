package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class LogModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: LogSettings): SettingsProvider
}
