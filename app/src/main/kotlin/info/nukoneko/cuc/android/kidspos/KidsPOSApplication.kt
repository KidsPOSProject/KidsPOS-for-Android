package info.nukoneko.cuc.android.kidspos

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.RestrictTo
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.api.APIAdapter
import info.nukoneko.cuc.android.kidspos.api.APIService
import info.nukoneko.cuc.android.kidspos.api.SettingsManager
import info.nukoneko.cuc.android.kidspos.entity.Staff
import info.nukoneko.cuc.android.kidspos.entity.Store
import info.nukoneko.cuc.android.kidspos.event.Event
import info.nukoneko.cuc.android.kidspos.manager.ItemManager
import info.nukoneko.cuc.android.kidspos.manager.SaleManager
import info.nukoneko.cuc.android.kidspos.manager.StaffManager
import info.nukoneko.cuc.android.kidspos.manager.StoreManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class KidsPOSApplication : Application() {
    private val staff = MutableLiveData<Staff>()
    fun getStaff(): LiveData<Staff> = staff
    fun setStaff(staff: Staff?) {
        this.staff.postValue(staff)
        storeManager.saveLatestStaff(staff)
    }

    private val store = MutableLiveData<Store>()
    fun getStore(): LiveData<Store> = store
    fun setStore(store: Store?) {
        this.store.postValue(store)
        storeManager.saveLatestStore(store)
    }

    private val handler = Handler(Looper.getMainLooper())

    private val apiService: APIService by lazy {
        val builder = OkHttpClient.Builder()
        val httpClient = builder.build()
        Retrofit.Builder()
                .baseUrl("http://$serverIpPortText/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build().create(APIService::class.java)
    }

    private lateinit var apiAdapter: APIAdapter

    lateinit var storeManager: StoreManager
    lateinit var staffManager: StaffManager
    lateinit var saleManager: SaleManager
    lateinit var itemManager: ItemManager
    lateinit var settingManager: SettingsManager

    private var defaultSubscribeScheduler: Scheduler? = null
    fun getDefaultSubscribeScheduler(): Scheduler {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io()
        }
        return defaultSubscribeScheduler!!
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    fun setDefaultSubscribeScheduler(subscribeScheduler: Scheduler) {
        defaultSubscribeScheduler = subscribeScheduler
    }

    var isPracticeModeEnabled: Boolean = false
        get() {
            return settingManager.isPracticeModeEnabled
        }

    var serverIp: String = ""
        get() {
            return settingManager.serverIP
        }

    var serverPort: Int = -1
        get() {
            return settingManager.serverPort
        }

    private var serverIpPortText = ""
        get() {
            return settingManager.ipPort
        }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())
        // もうこれいらんな...
        settingManager = SettingsManager(this)

        apiAdapter = object : APIAdapter(apiService) {}

        storeManager = StoreManager(this, apiAdapter)
        staffManager = StaffManager(apiAdapter)
        saleManager = SaleManager(apiAdapter)
        itemManager = ItemManager(apiAdapter)

        store.value = storeManager.lastStore
        staff.value = storeManager.lastStaff
    }

    private val eventObserver = MutableLiveData<Event>()
    fun postEvent(event: Event) {
        eventObserver.postValue(event)
    }

    fun getGlobalEventObserver(): LiveData<Event> = eventObserver

    companion object {
        operator fun get(context: Context?): KidsPOSApplication? {
            return context?.applicationContext as KidsPOSApplication
        }
    }
}