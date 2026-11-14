package com.dev.wine.music.app.fakes

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Stands in for the IO and Default dispatchers in unit tests: it runs the work right where it
 * is called, so repositories, use cases and ViewModels stay synchronous under test.
 */
val testDispatcher: CoroutineDispatcher = Dispatchers.Unconfined

/**
 * Runs the work in place like [testDispatcher] but counts how many times it was asked to,
 * so a test can prove that a class really moved its work to the injected dispatcher.
 */
class RecordingDispatcher : CoroutineDispatcher() {
    var dispatchCount = 0
        private set

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatchCount++
        block.run()
    }
}
