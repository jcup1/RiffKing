package com.theandroiddev.riffking.core.mvp

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable

abstract class MvpPresenter<V : MvpView> : MvpBasePresenter<V>() {

    val disposables = CompositeDisposable()

    override fun detachView() {
        super.detachView()
        disposables.clear()
    }
}