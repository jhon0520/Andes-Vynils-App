package com.dev.wine.music.app.util

/**
 * Indexed loops for the lists the app walks on every load (mappers, use cases, screens).
 *
 * `for (index in indices)` over a List compiles to a plain int loop, so no Iterator object is
 * created per call, and [fastMap] sizes its result once instead of letting it grow. Same idea as
 * Compose's fastMap/fastForEach. Meant for random-access lists (ArrayList), which is what
 * kotlinx.serialization and these helpers return.
 */
inline fun <T, R> List<T>.fastMap(transform: (T) -> R): List<R> {
    val result = ArrayList<R>(size)
    for (index in indices) {
        result.add(transform(get(index)))
    }
    return result
}

/** Indexed loop without an Iterator. See [fastMap]. */
inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    for (index in indices) {
        action(get(index))
    }
}
