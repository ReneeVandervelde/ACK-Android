package com.inkapplications.ack.android.settings.transmit

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.ExtendedActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Screen for configuring the repeated transmission settings.
 */
@AndroidEntryPoint
class TransmitSettingsActivity: ExtendedActivity(), TransmitSettingsController {
    val viewModel: TransmitSettingsViewModel by viewModels()

    override fun onCreate() {
        super.onCreate()

        setContent {
            TransmitSettingsScreen(
                viewModel = viewModel,
                controller = this
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.screenState
                    .filter { it is TransmitSettingsFormState.Finished }
                    .collect { finish() }
            }
        }
    }

    override fun onSaveForm(message: String, symbol: String) {
        Kimchi.trackEvent("transmit_settings_update")
        viewModel.save(message, symbol)
    }
}
