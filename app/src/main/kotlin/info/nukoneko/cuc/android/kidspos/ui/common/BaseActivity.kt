package info.nukoneko.cuc.android.kidspos.ui.common

import android.support.v7.app.AppCompatActivity
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {

    @Deprecated("app は ViewModel内で利用しましょう", ReplaceWith("", ""))
    protected val app: KidsPOSApplication?
        get() = KidsPOSApplication.get(this)

    protected open fun shouldEventSubscribes(): Boolean {
        return false
    }

    public override fun onStart() {
        super.onStart()
        if (shouldEventSubscribes()) {
            EventBus.getDefault().register(this)
        }
    }

    public override fun onStop() {
        super.onStop()
        if (shouldEventSubscribes()) {
            EventBus.getDefault().unregister(this)
        }
    }
}
