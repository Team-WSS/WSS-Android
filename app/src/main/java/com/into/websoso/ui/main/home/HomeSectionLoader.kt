package com.into.websoso.ui.main.home

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import retrofit2.HttpException

internal enum class HomeSection {
    POPULAR,
    FEEDS,
    TASTE,
}

internal fun isGlobalHomeFailure(
    section: HomeSection,
    error: Exception,
): Boolean = section != HomeSection.TASTE || isSessionFailure(error)

internal fun isSessionFailure(error: Throwable): Boolean = error is HttpException && (error.code() == 401 || error.code() == 403)

/** Requests belong to the Home ViewModel; replacing one never cancels another section. */
internal class HomeSectionLoader(
    private val scope: CoroutineScope,
) {
    private val jobs = mutableMapOf<HomeSection, Job>()

    fun <T> load(
        section: HomeSection,
        request: suspend () -> T,
        success: (T) -> Unit,
        failure: (Exception) -> Unit,
    ): Deferred<Boolean> {
        jobs[section]?.cancel()
        return scope
            .async {
                try {
                    val result = request()
                    currentCoroutineContext().ensureActive()
                    success(result)
                    true
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    currentCoroutineContext().ensureActive()
                    failure(error)
                    false
                }
            }.also { jobs[section] = it }
    }
}
