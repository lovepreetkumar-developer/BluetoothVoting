package com.example.bluetoothlechat

import android.app.Application
import com.example.bluetoothlechat.ui.custom_views.circle_progress.CustomProgress
import com.example.bluetoothlechat.utils.FieldsValidator
import com.example.bluetoothlechat.utils.PreferenceProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import timber.log.Timber
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * Property of Techfathers, Inc @ 2022 All Rights Reserved.
 */

class MainApplication : Application(), KodeinAware {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override val kodein = Kodein.lazy {

        import(androidXModule(this@MainApplication))
        bind() from singleton { FieldsValidator() }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { CustomProgress() }
    }
}