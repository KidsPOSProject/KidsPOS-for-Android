package info.nukoneko.cuc.android.kidspos.extensions

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import info.nukoneko.cuc.android.kidspos.ui.common.VMEvent

fun <T : Any?> mutableLiveDataOf(): MutableLiveData<T> = MutableLiveData()

@MainThread
fun <T : Any?> mutableLiveDataOf(defaultValue: T): MutableLiveData<T> {
    return MutableLiveData<T>().apply {
        value = defaultValue
    }
}

inline fun <T : LiveData<E>?, E : Any?, R> T.observe(
    owner: LifecycleOwner,
    crossinline block: (E) -> R
) {
    try {
        this?.observe(owner, Observer<E> { block(it) })
    } catch (e: Throwable) {

    } finally {

    }
}

// https://confluence.colopl.co.jp/pages/viewpage.action?pageId=22800884
inline fun <T : LiveData<VMEvent<E>>?, E : Any?, R> T.safetyObserve(
    owner: LifecycleOwner,
    crossinline block: (E) -> R
) {
    try {
        this?.observe(owner, Observer<VMEvent<E>> { event ->
            event?.getContentIfNotHandled()?.let {
                block(it)
            }
        })
    } catch (e: Throwable) {

    } finally {

    }
}

fun <T : MutableLiveData<VMEvent<E>>?, E : Any?> T.postValue(value: E) {
    try {
        this?.postValue(VMEvent(value))
    } catch (e: Throwable) {

    } finally {

    }
}
