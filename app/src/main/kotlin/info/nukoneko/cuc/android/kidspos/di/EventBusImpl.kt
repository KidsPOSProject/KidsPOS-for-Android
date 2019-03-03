package info.nukoneko.cuc.android.kidspos.di

import info.nukoneko.cuc.android.kidspos.event.Event
import info.nukoneko.cuc.android.kidspos.event.EventBus

class EventBusImpl : EventBus {
    private val bus = org.greenrobot.eventbus.EventBus.getDefault()
    override fun register(obj: Any?) {
        bus.register(obj)
    }

    override fun unregister(obj: Any?) {
        bus.unregister(obj)
    }

    override fun post(event: Event) {
        bus.post(event)
    }
}