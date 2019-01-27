package info.nukoneko.cuc.android.kidspos.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import info.nukoneko.cuc.android.kidspos.event.EventBus
import info.nukoneko.cuc.android.kidspos.event.Event

class EventBusImpl : EventBus {
    private val eventObserver = MutableLiveData<Event>()
    fun getGlobalEventObserver(): LiveData<Event> = eventObserver
    override fun post(event: Event) {
        eventObserver.postValue(event)
    }
}