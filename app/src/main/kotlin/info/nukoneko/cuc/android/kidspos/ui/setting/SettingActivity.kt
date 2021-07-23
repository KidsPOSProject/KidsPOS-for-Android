package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import org.koin.android.ext.android.inject

class SettingActivity : AppCompatActivity() {
    private val config: GlobalConfig by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingFragment.newInstance())
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val serverAddress = result.contents
            if (URLUtil.isValidUrl(serverAddress)) {
                config.currentServerAddress = serverAddress
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        fun createIntent(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}
