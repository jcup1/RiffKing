package com.theandroiddev.riffking.core.mvp

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import javax.inject.Inject

abstract class MvpFragment<V : MvpView, P : MvpPresenter<V>> : MvpFragment<V, P>(), MvpView {

    @Inject
    lateinit var _injectedPresenter: P

    @get:LayoutRes
    protected abstract val layoutResId: Int

    protected abstract fun inject()

    override fun createPresenter(): P = _injectedPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

}