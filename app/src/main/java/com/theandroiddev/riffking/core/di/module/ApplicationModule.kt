package com.theandroiddev.riffking.core.di.module

import android.app.Application
import android.content.Context
import com.theandroiddev.riffking.core.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideApp(): App {
        return application as App
    }

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return application
    }
}