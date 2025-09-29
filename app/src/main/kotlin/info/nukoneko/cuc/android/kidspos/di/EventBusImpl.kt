package info.nukoneko.cuc.android.kidspos.di

import info.nukoneko.cuc.android.kidspos.event.Event
import info.nukoneko.cuc.android.kidspos.event.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class EventBusImpl : EventBus {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _events = MutableSharedFlow<Event>(replay = 0, extraBufferCapacity = 64)
    private val events: SharedFlow<Event> = _events.asSharedFlow()
    
    private val subscribers = ConcurrentHashMap<Any, SubscriberInfo>()

    override fun post(event: Event) {
        scope.launch {
            _events.emit(event)
        }
    }

    override fun register(obj: Any?) {
        obj?.let { subscriber ->
            val methods = findSubscribeMethods(subscriber::class.java)
            if (methods.isNotEmpty()) {
                val job = scope.launch {
                    events.collect { event ->
                        if (subscribers.containsKey(subscriber)) {
                            methods.forEach { method ->
                                val parameterType = method.parameterTypes.firstOrNull()
                                if (parameterType?.isAssignableFrom(event::class.java) == true) {
                                    try {
                                        method.isAccessible = true
                                        method.invoke(subscriber, event)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }
                subscribers[subscriber] = SubscriberInfo(methods, job)
            }
        }
    }

    override fun unregister(obj: Any?) {
        obj?.let { subscriber ->
            subscribers.remove(subscriber)?.job?.cancel()
        }
    }

    private fun findSubscribeMethods(clazz: Class<*>): List<Method> {
        val methods = mutableListOf<Method>()
        var currentClass: Class<*>? = clazz
        
        while (currentClass != null) {
            currentClass.declaredMethods.forEach { method ->
                if (method.isAnnotationPresent(Subscribe::class.java)) {
                    if (method.parameterTypes.size == 1 && 
                        Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                        methods.add(method)
                    }
                }
            }
            currentClass = currentClass.superclass
        }
        
        return methods
    }

    private data class SubscriberInfo(
        val methods: List<Method>,
        val job: Job
    )
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val threadMode: ThreadMode = ThreadMode.MAIN)

enum class ThreadMode {
    MAIN
}
