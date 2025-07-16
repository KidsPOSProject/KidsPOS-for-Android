package info.nukoneko.cuc.android.kidspos.testutil

import info.nukoneko.cuc.android.kidspos.event.Event
import info.nukoneko.cuc.android.kidspos.event.EventBus

/**
 * Fake implementation of EventBus for testing
 */
class FakeEventBus : EventBus {
    private val events = mutableListOf<Event>()
    private val subscribers = mutableListOf<Any>()
    
    override fun register(obj: Any?) {
        obj?.let { subscribers.add(it) }
    }
    
    override fun unregister(obj: Any?) {
        obj?.let { subscribers.remove(it) }
    }
    
    override fun post(event: Event) {
        events.add(event)
    }
    
    fun getPostedEvents(): List<Event> = events.toList()
    
    fun clearEvents() {
        events.clear()
    }
    
    fun hasPostedEvent(predicate: (Event) -> Boolean): Boolean {
        return events.any(predicate)
    }
    
    fun getLastPostedEvent(): Event? = events.lastOrNull()
    
    fun isRegistered(obj: Any): Boolean = subscribers.contains(obj)
}