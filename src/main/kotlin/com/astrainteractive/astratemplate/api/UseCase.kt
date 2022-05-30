package com.astrainteractive.astratemplate.api

abstract class UseCase<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Type?
    suspend operator fun invoke(params: Params) = run(params)
}