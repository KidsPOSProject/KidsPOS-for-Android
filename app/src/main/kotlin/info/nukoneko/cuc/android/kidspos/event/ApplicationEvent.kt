package info.nukoneko.cuc.android.kidspos.event

sealed class ApplicationEvent<T : Any>(v: T? = null) : Event<T>(v) {
    object AppModeChange : ApplicationEvent<Any>()

    object AppUpdateAvailable : ApplicationEvent<Any>()
}