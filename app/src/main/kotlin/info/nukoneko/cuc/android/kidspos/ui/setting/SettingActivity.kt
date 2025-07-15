package info.nukoneko.cuc.android.kidspos.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import info.nukoneko.cuc.android.kidspos.di.GlobalConfig
import org.koin.android.ext.android.inject

class SettingActivity : AppCompatActivity() {
    private val config: GlobalConfig by inject()
    
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents != null) {
            val serverAddress = result.contents
            if (URLUtil.isValidUrl(serverAddress)) {
                config.currentServerAddress = serverAddress
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingFragment.newInstance())
            .commit()
    }
    
    fun launchBarcodeScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("QRコードをスキャンしてください")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }

    companion object {
        fun createIntent(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}
