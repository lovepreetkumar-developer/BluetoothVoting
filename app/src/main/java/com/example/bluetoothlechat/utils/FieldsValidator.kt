package com.example.bluetoothlechat.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern

/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class FieldsValidator {

    // Regular Expression
    val EMAIL_REGEX =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    val INTEGERS_REGEX =
        ".*\\d.*"
    val PHONE_REGEX = "\\d{3}-\\d{7}"
    val PASSWORD_REGEX =
        "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d][A-Za-z\\d!@#$%^&*()_+]{7,19}$"
    val PASSWORD_REGEX_NEW = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{6,}$"
    val AT_LEAST_ONE_DIGIT_AND_UPPER_ALPHA_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$"
    val AT_LEAST_ONE_SYMBOL_AND_UPPER_ALPHA_REGEX =
        "^(?=.*[$@$!%*#?&])(?=.*[a-z])(?=.*[A-Z]).{6,20}$"

    // Error Messages
    val REQUIRED_MSG = "Required"
    val EMAIL_MSG = "Email address is required and in correct format"
    val PHONE_MSG = "##########"
    val PASSWORD_MSG =
        "Password must be of 9 character and include atleast one small and one capital letter and number"
    val TEXT_LIMIT = "Nickname cannot be greater than 18"

    // call this method when you need to check email validation
    fun isEmailAddress(editText: EditText, required: Boolean): Boolean {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required)
    }

    // call this method when you need to check email validation
    fun isEmailAddressWithoutErrorMessage(editText: EditText, required: Boolean): Boolean {
        return isValidWithoutErrorMessage(editText, EMAIL_REGEX, required)
    }

    // call this method when you need to check email validation
    fun isOnlyIntegersWithoutErrorMessage(editText: EditText, required: Boolean): Boolean {
        return isValidWithoutErrorMessage(editText, INTEGERS_REGEX, required)
    }

    fun isValidPassword(editText: EditText, required: Boolean): Boolean {
        return if (editText.text.toString().length < 8) {
            editText.error = PASSWORD_MSG
            false
        } else {
            isValid(editText, PASSWORD_REGEX_NEW, PASSWORD_MSG, required)
        }
    }

    // call this method when you need to check phone number validation
    fun isPhoneNumber(editText: EditText, required: Boolean): Boolean {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required)
    }

    // return true if the input field is valid, based on the parameter passed
    fun isValid(editText: EditText, regex: String?, errMsg: String?, required: Boolean): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        editText.error = null
        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false
        // pattern doesn't match so returning false
        if (!Pattern.matches(regex, text)) {
            editText.error = errMsg
            return false
        }
        return true
    }

    // return true if the input field is valid, based on the parameter passed
    fun isValidWithoutErrorMessage(
        editText: EditText,
        regex: String?,
        required: Boolean
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        editText.error = null
        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false
        // pattern doesn't match so returning false
        if (!Pattern.matches(regex, text)) {
            return false
        }
        return true
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    fun hasText(editText: TextView): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null
        // length 0 means there is no text
        if (text.length == 0) {
            editText.error = REQUIRED_MSG
            editText.requestFocus()
            return false
        }
        return true
    }

    fun isChecked(context: Context?, checkBox: CheckBox): Boolean {
        return if (!checkBox.isChecked) {
            //Toast.makeText(context, context.getResources().getString(R.string.accept_terms_conditions), Toast.LENGTH_SHORT).show();
            false
        } else true
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    fun hasText(editText: EditText, errMsg: String?): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null
        // length 0 means there is no text
        if (text.length == 0) {
            editText.error = errMsg
            return false
        }
        return true
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    fun hasTextWithoutError(textView: AppCompatTextView): Boolean {
        val text = textView.text.toString().trim { it <= ' ' }
        // length 0 means there is no text
        if (text.isEmpty()) {
            return false
        }
        return true
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    fun hasTextWithoutError(editText: AppCompatEditText): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // length 0 means there is no text
        if (text.isEmpty()) {
            return false
        }
        return true
    }

    fun isTextWithInRange(
        editText: EditText,
        minLimit: Int,
        maxLimit: Int,
        error: String?
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null
        // length 0 means there is no text
        if (text.length == 0) {
            editText.error = REQUIRED_MSG
            return false
        }
        return if (text.length >= minLimit && text.length <= maxLimit) {
            true
        } else {
            editText.error = error
            false
        }
    }

    fun isMatching(editText1: EditText, editText2: EditText): Boolean {
        return if (editText1.text.toString().equals(editText2.text.toString(), ignoreCase = true)) {
            true
        } else {
            editText2.error = "Old password and confirmation password don't match"
            false
        }
    }


    // return true if the input field is valid, based on the parameter passed
    fun haveAtLeastOneDigitAndUpperAlpha(
        editText: EditText,
        regex: String?,
        errMsg: String?,
        required: Boolean
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        editText.error = null
        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false
        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.error = errMsg
            return false
        }
        return true
    }


    // return true if the input field is valid, based on the parameter passed
    fun haveAtLeastOneSymbolAndUpperAlpha(
        editText: EditText,
        regex: String?,
        errMsg: String?,
        required: Boolean
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        editText.error = null
        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false
        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.error = errMsg
            return false
        }
        return true
    }


    @Synchronized
    private fun clearError(view: EditText) {
        Handler().postDelayed({ view.error = null }, 1000)
    }

    @Synchronized
    fun validateEmail(view: EditText, message: String?): Boolean {
        var message = message
        if (message == null || message.isEmpty()) message = "Please enter a valid email"
        val fgcspan = ForegroundColorSpan(Color.WHITE)
        val ssbuilder = SpannableStringBuilder(message)
        ssbuilder.setSpan(fgcspan, 0, message.length, 0)
        if (!Patterns.EMAIL_ADDRESS.matcher(
                view.text.toString()
            ).matches()
        ) {
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }


    @Synchronized
    fun validateNotEmpty(view: TextView, message: String?): Boolean {
        var message = message
        if (message == null || message.isEmpty()) message = "This field should not be empty"
        val fgcspan = ForegroundColorSpan(Color.WHITE)
        val ssbuilder = SpannableStringBuilder(message)
        ssbuilder.setSpan(fgcspan, 0, message.length, 0)
        if (view.text.toString().isEmpty()) {
            view.requestFocus()
            view.error = ssbuilder
            Handler().postDelayed({ view.error = null }, 1000)
            return true
        }
        return false
    }

    @Synchronized
    fun validateNotEmpty(view: EditText, message: String?): Boolean {
        var message = message
        if (message == null || message.isEmpty() || message.equals("", ignoreCase = true)) message =
            "This field should not be empty"
        val fgcspan = ForegroundColorSpan(Color.WHITE)
        val ssbuilder = SpannableStringBuilder(message)
        ssbuilder.setSpan(fgcspan, 0, message.length, 0)
        if (view.text.toString().isEmpty()) {
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validateLength(
        view: EditText, minLength: Int, maxLength: Int,
        message: String?
    ): Boolean {
        var message = message
        if (view.text.toString() == "" || view.text.toString().length < minLength) {
            if (message == null || message.isEmpty()) message =
                ("Input should be more than " + minLength
                        + " characters.")
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        if (view.text.toString().length > maxLength) {
            if (message == null || message.isEmpty()) message =
                ("Input should be less than " + maxLength
                        + " characters.")
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validateLength(
        view: TextView, minLength: Int, maxLength: Int,
        message: String?
    ): Boolean {
        var message = message
        if (view.text.toString() == "" || view.text.toString().length < minLength) {
            if (message == null || message.isEmpty()) message =
                ("Input should be more than " + minLength
                        + " characters.")
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.error = ssbuilder
            return true
        }
        if (view.text.toString().length > maxLength) {
            if (message == null || message.isEmpty()) message =
                ("Input should be less than " + maxLength
                        + " characters.")
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.error = ssbuilder
            return true
        }
        return false
    }


    @Synchronized
    fun validateValidCharacters(
        view: EditText, pattern: Pattern,
        message: String?
    ): Boolean {
        var message = message
        if (!pattern.matcher(view.text.toString()).matches()) {
            if (message == null || message.isEmpty()) message =
                "This input does not fit the requiered pattern."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validateUsernameWithoutSpace(
        view: EditText,
        message: String?
    ): Boolean {
        var message = message
        if (view.text.toString().contains(" ")) {
            if (message == null || message.isEmpty()) message =
                "This input does not fit the requiered pattern."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validateUsernameSpace(view: EditText, message: String?): Boolean {
        var message = message
        if (view.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
            if (message == null || message.isEmpty()) message = "This field should not be empty."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validatedateofbirth(view: EditText, message: String?): Boolean {
        var message = message
        if (view.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
            if (message == null || message.isEmpty()) message = "This field should not be empty."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validateGender(view: Spinner, message: String?): Boolean {
        var message = message
        val spinnerview = view.selectedView
        val selectedTextView = spinnerview as TextView
        val gender = view.selectedItem as String
        if (gender.equals("sex", ignoreCase = true)) {
            if (message == null || message.isEmpty()) message = "Please choose Gender."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            //((TextView) spinnerview).setError(ssbuilder);
            selectedTextView.error = ssbuilder
            return true
        }
        return false
    }

    @Synchronized
    fun validatePhoneNumberSpace(view: EditText, message: String?): Boolean {
        var message = message
        if (view.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
            if (message == null || message.isEmpty()) message = "This field should not be empty."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        if (view.text.toString().length < 10 || view.text.toString().length > 13) {
            message = "Please enter the right phone number."
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(message)
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            view.requestFocus()
            view.error = ssbuilder
            clearError(view)
            return true
        }
        return false
    }

    @Synchronized
    fun validatePasswordMatch(
        password: EditText,
        confirmPassword: EditText,
        message: String?
    ): Boolean {
        var message = message
        if (!password.text.toString().equals(confirmPassword.text.toString(), ignoreCase = true)) {
            if (message == null || message.isEmpty()) message = "Password Mismatch"
            val fgcspan = ForegroundColorSpan(Color.WHITE)
            val ssbuilder = SpannableStringBuilder(
                message
            )
            ssbuilder.setSpan(fgcspan, 0, message.length, 0)
            confirmPassword.requestFocus()
            confirmPassword.error = ssbuilder
            clearError(confirmPassword)
            return false
        }
        return true
    }
}