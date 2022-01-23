package com.inkapplications.aprs.android.input

/**
 * Validator that always passes regardless of the input
 */
object NoValidation: Validator<Any?> {
    override fun validate(input: Any?) = ValidationResult.Valid
}
