package com.theandroiddev.riffking.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addDisposableTo(compositeDisposable: CompositeDisposable) =
        compositeDisposable.add(this)
