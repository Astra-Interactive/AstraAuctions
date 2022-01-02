package com.astrainteractive.astratemplate.utils

import java.lang.Exception

abstract class Callback{
    abstract fun <T>onSuccess(result: T?)
    abstract fun onFailure(e: Exception)
}