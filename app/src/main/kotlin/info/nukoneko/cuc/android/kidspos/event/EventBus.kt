package info.nukoneko.cuc.android.kidspos.event

interface EventBus {
    fun <T : Any> post(event: Event<T>)

    fun register(obj: Any?)

    fun unregister(obj: Any?)
}