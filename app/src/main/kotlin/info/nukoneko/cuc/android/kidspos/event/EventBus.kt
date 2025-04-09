package info.nukoneko.cuc.android.kidspos.event

interface EventBus {
    fun post(event: Event)

    fun register(obj: Any?)

    fun unregister(obj: Any?)
}
