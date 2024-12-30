package com.example.bluetoothlechat.ui

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.BaseCallback
import com.example.bluetoothlechat.base.BaseFragment
import com.example.bluetoothlechat.databinding.FragmentLoginBinding
import com.example.bluetoothlechat.models.UserModel
import com.example.bluetoothlechat.ui.custom_views.circle_progress.CustomProgress
import com.example.bluetoothlechat.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class LoginFragment : BaseFragment<FragmentLoginBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mProgressView: CustomProgress by instance()

    private val mPrefProvider: PreferenceProvider by instance()
    private val mFieldsValidator: FieldsValidator by instance()

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_login
    }

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBaseCallback(baseCallback)
    }

    private val baseCallback = BaseCallback { view ->
        when (view.id) {
            R.id.img_cross -> goBack()
            R.id.img_eye -> eyeImageWork()
            R.id.btn_login -> if (validateFields()) {
                activity?.hideSoftKeyboard(binding.rlParent)
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                if (email == "admin@gmail.com" && password == "12345678") {
                    val userModel = UserModel()
                    userModel.id = (1..2000).random()
                    userModel.email = email
                    userModel.login_type = Constants.LOGIN_TYPE_ADMIN
                    mPrefProvider.setUser(userModel)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToEnableBluetoothFragment())
                } else if (
                    (email == "user1@gmail.com" && password == "12345678") ||
                    (email == "user2@gmail.com" && password == "12345678") ||
                    (email == "user3@gmail.com" && password == "12345678") ||
                    (email == "user4@gmail.com" && password == "12345678") ||
                    (email == "user5@gmail.com" && password == "12345678") ||
                    (email == "user6@gmail.com" && password == "12345678") ||
                    (email == "user7@gmail.com" && password == "12345678") ||
                    (email == "user8@gmail.com" && password == "12345678") ||
                    (email == "user9@gmail.com" && password == "12345678") ||
                    (email == "user10@gmail.com" && password == "12345678") ||
                    (email == "user11@gmail.com" && password == "12345678") ||
                    (email == "user12@gmail.com" && password == "12345678") ||
                    (email == "user13@gmail.com" && password == "12345678") ||
                    (email == "user14@gmail.com" && password == "12345678") ||
                    (email == "user15@gmail.com" && password == "12345678") ||
                    (email == "user16@gmail.com" && password == "12345678") ||
                    (email == "user17@gmail.com" && password == "12345678") ||
                    (email == "user18@gmail.com" && password == "12345678") ||
                    (email == "user19@gmail.com" && password == "12345678") ||
                    (email == "user20@gmail.com" && password == "12345678") ||
                    (email == "user21@gmail.com" && password == "12345678") ||
                    (email == "user22@gmail.com" && password == "12345678") ||
                    (email == "user23@gmail.com" && password == "12345678") ||
                    (email == "user24@gmail.com" && password == "12345678") ||
                    (email == "user25@gmail.com" && password == "12345678")
                ) {
                    val userModel = UserModel()
                    userModel.id = getUserId(email)
                    userModel.email = email
                    userModel.login_type = Constants.LOGIN_TYPE_USER
                    mPrefProvider.setUser(userModel)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToEnableBluetoothFragment())
                } else {
                    requireContext().showMessageDialog("Wrong Credentials.")
                }
            }
        }
    }

    private fun getUserId(email: String): Int {
        when (email) {
            "user1@gmail.com" -> return Constants.USER_ID_1
            "user2@gmail.com" -> return Constants.USER_ID_2
            "user3@gmail.com" -> return Constants.USER_ID_3
            "user4@gmail.com" -> return Constants.USER_ID_4
            "user5@gmail.com" -> return Constants.USER_ID_5
            "user6@gmail.com" -> return Constants.USER_ID_6
            "user7@gmail.com" -> return Constants.USER_ID_7
            "user8@gmail.com" -> return Constants.USER_ID_8
            "user9@gmail.com" -> return Constants.USER_ID_9
            "user10@gmail.com" -> return Constants.USER_ID_10
            "user11@gmail.com" -> return Constants.USER_ID_11
            "user12@gmail.com" -> return Constants.USER_ID_12
            "user13@gmail.com" -> return Constants.USER_ID_13
            "user14@gmail.com" -> return Constants.USER_ID_14
            "user15@gmail.com" -> return Constants.USER_ID_15
            "user16@gmail.com" -> return Constants.USER_ID_16
            "user17@gmail.com" -> return Constants.USER_ID_17
            "user18@gmail.com" -> return Constants.USER_ID_18
            "user19@gmail.com" -> return Constants.USER_ID_19
            "user20@gmail.com" -> return Constants.USER_ID_20
            "user21@gmail.com" -> return Constants.USER_ID_21
            "user22@gmail.com" -> return Constants.USER_ID_22
            "user23@gmail.com" -> return Constants.USER_ID_23
            "user24@gmail.com" -> return Constants.USER_ID_24
            "user25@gmail.com" -> return Constants.USER_ID_25
        }
        return Constants.USER_ID_1
    }

    private fun validateFields(): Boolean {
        return mFieldsValidator.hasText(binding.etEmail)
                && mFieldsValidator.hasText(binding.etPassword)
    }

    private fun eyeImageWork() {
        if (binding.etPassword.transformationMethod === PasswordTransformationMethod.getInstance()) {
            //Show Password
            binding.imgEye.setImageResource(R.drawable.ic_eye_visible)
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            //Hide Password
            binding.imgEye.setImageResource(R.drawable.ic_eye_invisible)
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.etPassword.setSelection(
            binding.etPassword.text.toString().length
        )
    }
}