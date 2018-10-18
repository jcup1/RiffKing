package com.theandroiddev.riffking.core.di.component

import android.content.Context
import com.theandroiddev.riffking.core.App
import com.theandroiddev.riffking.core.di.module.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class
        ]
)
interface ApplicationComponent {

    fun inject(app: App)

    fun app(): App

    fun context(): Context

}