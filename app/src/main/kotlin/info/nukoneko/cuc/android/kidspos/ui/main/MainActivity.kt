package info.nukoneko.cuc.android.kidspos.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.CalculatorDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.main.itemlist.ItemListViewAdapter
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingActivity
import info.nukoneko.cuc.android.kidspos.util.AlertUtil
import info.nukoneko.cuc.android.kidspos.util.BarcodeKind
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseBarcodeReadableActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myViewModel: MainActivityViewModel by viewModel()

    private val adapter: ItemListViewAdapter by lazy {
        ItemListViewAdapter()
    }

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

    private val listener: MainActivityViewModel.Listener = object : MainActivityViewModel.Listener {
        override fun onDataChanged(data: List<Item>) {
            adapter.setItems(data)
        }

        override fun onSendSaleSuccess() {
            adapter.clear()
        }

        override fun onAppModeChange() {
            adapter.clear()
        }

        override fun onReadItemSuccess() {
            if (adapter.itemCount > 0) {
                binding.appBarLayout.contentMain.recyclerView.smoothScrollToPosition(0)
            }
        }

        override fun onShouldShowMessage(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onShouldShowMessage(messageId: Int) {
            Toast.makeText(this@MainActivity, messageId, Toast.LENGTH_SHORT).show()
        }

        override fun onNotReachableServer() {
            showNotReachableErrorDialog()
        }

        override fun onStartAccount() {
            CalculatorDialogFragment
                    .newInstance(myViewModel.data)
                    .show(supportFragmentManager, "Calculator")
        }

        override fun onChangeTitleSuffix(title: String) {
            binding.appBarLayout.toolbar.title = title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.appBarLayout.toolbar)
        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarLayout.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(navigationListener)
        myViewModel.listener = listener
        binding.appBarLayout.contentMain.viewModel = myViewModel
        binding.appBarLayout.contentMain.recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
        binding.navView.menu.setGroupVisible(R.id.beta_test, ProjectSettings.TEST_MODE)
    }

    /**
     * 読み取ったバーコードを用いてデータを取得する。
     * スタッフの登録作業が必要になる可能性がある
     *
     * @param barcode barcode
     * @param prefix  barcode type
     */
    override fun onBarcodeInput(barcode: String, prefix: BarcodeKind) {
        myViewModel.onBarcodeInput(barcode, prefix)
    }

    private fun showNotReachableErrorDialog() {
        AlertUtil.showErrorDialog(this,
                "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                false, DialogInterface.OnClickListener { _, _ -> SettingActivity.createIntent(this@MainActivity) })
    }

    companion object {
        fun createIntentWithClearTask(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
