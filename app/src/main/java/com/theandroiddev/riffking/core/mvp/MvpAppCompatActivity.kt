package com.theandroiddev.riffking.core.mvp

import android.os.Bundle
import android.support.annotation.LayoutRes
import com.github.ajalt.timberkt.Timber
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.theandroiddev.riffking.BuildConfig
import timber.log.Timber.DebugTree
import javax.inject.Inject


abstract class MvpAppCompatActivity<V : MvpView, P : MvpPresenter<V>> : MvpActivity<V, P>(),
        MvpView {

    @Inject
    lateinit var _injectedPresenter: P

    @get:LayoutRes
    protected abstract val layoutResId: Int?

    override fun createPresenter(): P = _injectedPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutResId?.let { setContentView(it) }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}