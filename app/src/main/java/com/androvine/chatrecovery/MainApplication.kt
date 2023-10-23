package com.androvine.chatrecovery

import android.app.Application
import com.androvine.chatrecovery.permissionMVVM.permissionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules (permissionModule)

        }

    }
}