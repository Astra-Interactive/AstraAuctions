package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astramarket.domain.DataSource
import com.astrainteractive.astramarket.domain.IDataSource
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.di.getValue

object DataSourceModule : IModule<IDataSource>() {
    private val database by DatabaseModule
    override fun initializer(): IDataSource {
        return DataSource(database)
    }
}