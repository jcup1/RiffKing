package com.theandroiddev.riffking.core

import android.app.Application
import com.theandroiddev.riffking.core.di.component.ApplicationComponent
import com.theandroiddev.riffking.core.di.component.DaggerApplicationComponent
import com.theandroiddev.riffking.core.di.module.ApplicationModule

open class App : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        applicationComponent.inject(this)
    }
}