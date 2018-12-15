package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingFragment.newInstance())
                .commit()
    }

    companion object {
        fun createIntent(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}
