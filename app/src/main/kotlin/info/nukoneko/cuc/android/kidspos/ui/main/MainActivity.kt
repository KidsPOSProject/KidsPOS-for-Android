package info.nukoneko.cuc.android.kidspos.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity
import info.nukoneko.cuc.android.kidspos.ui.main.itemlist.ItemListFragment
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingActivity
import info.nukoneko.cuc.android.kidspos.util.AlertUtil
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import org.koin.android.ext.android.inject

class MainActivity : BaseBarcodeReadableActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listener = object : MainViewModel.Listener {
        override fun onNotReachableServer() {
            showNotReachableErrorDialog()
        }

        override fun onShouldChangeTitleSuffix(titleSuffix: String) {
            binding.toolbar.title = "${getString(R.string.app_name)} $titleSuffix"
        }
    }
    private val myViewModel: MainViewModel by inject()
    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.setting -> SettingActivity.createIntent(this@MainActivity)
            R.id.change_store -> {
                val fragment = StoreListDialogFragment.newInstance()
                fragment.isCancelable = false
                fragment.show(supportFragmentManager, "changeStore")
            }
            R.id.input_dummy_item -> onBarcodeInput("1234567890", BarcodeKind.ITEM)
            R.id.input_dummy_store -> onBarcodeInput("1234567890", BarcodeKind.STAFF)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(navigationListener)
        binding.viewModel = myViewModel.also {
            it.listener = listener
        }
        binding.setLifecycleOwner(this)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ItemListFragment.newInstance(), "itemList")
                .commit()
    }

    override fun onStart() {
        super.onStart()
        myViewModel.onStart()
    }

    override fun onStop() {
        myViewModel.onStop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
        binding.navView.menu.setGroupVisible(R.id.beta_test, ProjectSettings.DEMO_MODE)
    }

    override fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        myViewModel.onBarcodeInput(barcode, prefix)
    }

    private fun showNotReachableErrorDialog() {
        AlertUtil.showErrorDialog(this,
                "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                false, DialogInterface.OnClickListener { _, _ ->
            SettingActivity.createIntent(this)
        })
    }

    companion object {
        fun createIntentWithClearTask(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
