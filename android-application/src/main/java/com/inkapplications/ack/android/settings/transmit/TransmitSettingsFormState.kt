package com.inkapplications.ack.android.settings.transmit

/**
 * Possible states of the transmit settings form.
 */
sealed interface TransmitSettingsFormState {
    /**
     * Placeholder state before the form data is loaded.
     */
    object Inital: TransmitSettingsFormState

    /**
     * Form state with elements displayed.
     */
    sealed interface FormState: TransmitSettingsFormState {
        val message: String
        val symbol: String
        val messageError: String?
        val symbolError: String?
    }

    /**
     * Fully editable form state.
     */
    data class Editable(
        override val message: String,
        override val symbol: String,
        override val messageError: String? = null,
        override val symbolError: String? = null,
    ): FormState

    /**
     * Form state with pending edits.
     *
     * The form should not be editable in this state, and therefore errors
     * will always be blank.
     */
    data class Draft(
        override val message: String,
        override val symbol: String,
    ): FormState {
        override val messageError: String? = null
        override val symbolError: String? = null
    }

    /**
     * Final state of the screen before closing.
     */
    object Finished: TransmitSettingsFormState
}
