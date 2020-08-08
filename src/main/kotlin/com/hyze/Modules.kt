package com.hyze

import com.hyze.events.EventManager
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun setupKoin(){
    startKoin{
        modules(eventModules)
    }
}

val eventModules = module {
    single { EventManager() }
}