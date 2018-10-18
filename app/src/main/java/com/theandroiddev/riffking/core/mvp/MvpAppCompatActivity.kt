package com.theandroiddev.riffking.core.mvp

import android.os.Bundle
import android.support.annotation.LayoutRes
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import javax.inject.Inject

abstract class MvpAppCompatActivity<V : MvpView, P : MvpPresenter<V>> : MvpActivity<V, P>(),
        MvpView {

    @Inject
    lateinit var _injectedPresenter: P

    @get:LayoutRes
    protected abstract val layoutResId: Int?

    protected abstract fun inject()

    override fun createPresenter(): P = _injectedPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        layoutResId?.let { setContentView(it) }

    }

}