package com.vacation.tripinmind

import android.app.Application
import android.util.Log
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

       ProviderInstaller.installIfNeededAsync(this, object :
            ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {
                Log.d("AppApplication", "Security provider installed successfully.")
            }

            override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: android.content.Intent?) {
                Log.e("AppApplication", "Security provider installation failed with error code: $errorCode")
            }
        })
    }
}