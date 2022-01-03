package com.astrainteractive.astratemplate.sqldatabase.entities

import java.lang.Exception

abstract class Callback{
    abstract fun <T>onSuccess(result: T?)
    abstract fun onFailure(e: Exception)
}