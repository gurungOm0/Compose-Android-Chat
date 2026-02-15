package com.softstudio.chat.services

import com.google.firebase.perf.metrics.Trace
import com.google.firebase.perf.FirebasePerformance

inline fun <T> safeTrace(name: String, block: Trace.() -> T): Result<T> {
    val firebaseTrace = FirebasePerformance.getInstance().newTrace(name)
    firebaseTrace.start()
    return try {
        // Run the logic and wrap success in Result
        Result.success(firebaseTrace.run(block))
    } catch (e: Exception) {
        // Record the error if you want, then wrap failure
        Result.failure(e)
    } finally {
        firebaseTrace.stop()
    }
}