package com.khs.lovelynote

import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.multidex.MultiDexApplication
import timber.log.Timber


class LovelyNoteApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Timber.plant(Timber.DebugTree())
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun onCreate() {
        super.onCreate()
    }
}