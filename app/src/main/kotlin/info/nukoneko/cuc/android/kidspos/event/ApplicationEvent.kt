package info.nukoneko.cuc.android.kidspos.event

sealed class ApplicationEvent : Event() {
    object AppModeChange : ApplicationEvent()

    object AppUpdateAvailable : ApplicationEvent()
}