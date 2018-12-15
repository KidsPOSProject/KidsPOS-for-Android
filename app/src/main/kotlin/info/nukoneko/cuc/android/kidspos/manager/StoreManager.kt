package info.nukoneko.cuc.android.kidspos.manager

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

import com.google.gson.Gson

import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import io.reactivex.Observable

class StoreManager(private val context: Context, apiAdapter: APIAdapter) : BaseManager(apiAdapter) {

    private val preference: SharedPreferences
        get() = context.getSharedPreferences(KEY_PREFERENCE_STORE_MANAGER, Context.MODE_PRIVATE)

    private val gsonInstance by lazy {
        Gson()
    }

    val lastStaff: Staff?
        get() {
            return try {
                val staff = preference.getString(KEY_LATEST_STAFF, "")
                if (TextUtils.isEmpty(staff)) null else gsonInstance.fromJson(staff, Staff::class.java)
            } catch (e: ClassCastException) {
                preference.edit().putString(KEY_LATEST_STAFF, "").apply()
                null
            }
        }

    val lastStore: Store?
        get() {
            return try {
                val store = preference.getString(KEY_LATEST_STORE, "")
                if (TextUtils.isEmpty(store)) null else gsonInstance.fromJson(store, Store::class.java)
            } catch (e: ClassCastException) {
                preference.edit().putString(KEY_LATEST_STORE, "").apply()
                null
            }
        }

    fun saveLatestStaff(staff: Staff?) {
        val editor = preference.edit().remove(KEY_LATEST_STAFF)
        if (staff != null) {
            editor.putString(KEY_LATEST_STAFF, gsonInstance.toJson(staff))
        }
        editor.apply()
    }

    fun saveLatestStore(store: Store?) {
        val editor = preference.edit().remove(KEY_LATEST_STORE)
        if (store != null) {
            editor.putString(KEY_LATEST_STORE, gsonInstance.toJson(store))
        }
        editor.apply()
    }

    fun fetchStores(): Observable<List<Store>> {
        return apiService.storeList()
    }

    companion object {
        private const val KEY_PREFERENCE_STORE_MANAGER = "preference_store_manager"
        private const val KEY_LATEST_STORE = "LATEST_STORE"
        private const val KEY_LATEST_STAFF = "LATEST_STAFF"
    }
}
