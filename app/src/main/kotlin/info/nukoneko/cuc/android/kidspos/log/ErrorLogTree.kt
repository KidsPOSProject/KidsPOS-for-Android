package info.nukoneko.cuc.android.kidspos.log

import android.util.Log
import timber.log.Timber
import javax.inject.Inject

class ErrorLogTree @Inject constructor(
    private val logRepository: LogRepository
) : Timber.Tree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.WARN

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logRepository.append(LogEntry(System.currentTimeMillis(), priority, tag, message))
    }
}
