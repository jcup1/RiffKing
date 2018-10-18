package com.theandroiddev.riffking.core.mvp

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

abstract class MvpPresenter<V : MvpView> : MvpBasePresenter<V>() {

    //TODO add handling composite disposable

}