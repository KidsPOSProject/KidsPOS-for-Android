package info.nukoneko.cuc.android.kidspos.event

import info.nukoneko.cuc.android.kidspos.event.Event

interface EventBus {
    fun post(event: Event)
}