package com.theandroiddev.riffking.core

import com.theandroiddev.riffking.core.di.component.ApplicationComponent
import com.theandroiddev.riffking.core.di.component.DaggerApplicationComponent
import com.theandroiddev.riffking.core.di.module.ApplicationModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

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