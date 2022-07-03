package com.inkapplications.ack.android.settings.agreement

import androidx.activity.compose.setContent
import com.inkapplications.android.extensions.ExtendedActivity

/**
 * Displays the user-agreement for the application.
 *
 * Note that this does not have a prompt to agree/disagree like seen in
 * the onboarding screens. It is simply for informational purposes.
 */
class UserAgreementActivity: ExtendedActivity() {
    override fun onCreate() {
        super.onCreate()

        setContent {
            UsageAgreementReviewScreen(::onBackPressed)
        }
    }
}
