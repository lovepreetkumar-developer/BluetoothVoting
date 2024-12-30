package com.example.bluetoothlechat.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.base.BaseFragment
import com.example.bluetoothlechat.databinding.FragmentSplashBinding
import com.example.bluetoothlechat.utils.Constants
import com.example.bluetoothlechat.utils.PreferenceProvider
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class SplashFragment : BaseFragment<FragmentSplashBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private var mHandler: Handler? = null

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_splash
    }

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        mHandler?.removeCallbacks(mRunnable)
        super.onDestroy()
    }

    private fun initView() {
        mHandler = Handler(Looper.getMainLooper())
        mHandler?.postDelayed(mRunnable, Constants.SPLASH_TIME_OUT)
    }

    private val mRunnable = Runnable {
        if (mPrefProvider.getUser() == null) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        } else {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToEnableBluetoothFragment())
        }
    }
}