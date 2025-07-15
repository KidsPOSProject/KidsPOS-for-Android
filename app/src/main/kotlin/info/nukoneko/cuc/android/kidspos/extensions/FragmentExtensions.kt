package info.nukoneko.cuc.android.kidspos.extensions

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.lazyWithArgs(key: String): Lazy<T> {
    return lazy {
        when (T::class) {
            String::class -> arguments!!.getString(key) as T
            Int::class -> arguments!!.getInt(key) as T
            Boolean::class -> arguments!!.getBoolean(key) as T
            else -> arguments!!.getSerializable(key) as T
        }
    }
}
