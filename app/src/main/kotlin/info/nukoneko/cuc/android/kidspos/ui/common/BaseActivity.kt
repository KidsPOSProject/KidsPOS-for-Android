package info.nukoneko.cuc.android.kidspos.ui.common

import android.support.v7.app.AppCompatActivity
import info.nukoneko.cuc.android.kidspos.KidsPOSApplication

abstract class BaseActivity : AppCompatActivity() {
    @Deprecated("app は ViewModel内で利用しましょう", ReplaceWith("", ""))
    protected val app: KidsPOSApplication?
        get() = KidsPOSApplication.get(this)
}
