package com.dev.wine.music.app.domain.coroutines

import javax.inject.Qualifier

/**
 * Dispatcher for network and disk work (Dispatchers.IO in the app).
 * Injected instead of hard-coded so unit tests can run it in place.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Dispatcher for CPU work such as sorting and matching lists (Dispatchers.Default in the app).
 * Keeps that work off the main thread, which only draws the result.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
