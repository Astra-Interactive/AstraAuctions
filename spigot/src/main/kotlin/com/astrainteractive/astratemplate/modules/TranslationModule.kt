package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.utils.Translation
import ru.astrainteractive.astralibs.di.IReloadable

object TranslationModule:IReloadable<Translation>() {
    override fun initializer(): Translation {
        return Translation()
    }
}