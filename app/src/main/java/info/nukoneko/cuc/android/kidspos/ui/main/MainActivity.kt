package info.nukoneko.cuc.android.kidspos.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.Toast
import info.nukoneko.cuc.android.kidspos.Constants
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.event.*
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.CalculatorDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingsActivity
import info.nukoneko.cuc.android.kidspos.util.AlertUtil
import info.nukoneko.cuc.android.kidspos.util.BarcodePrefix
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class MainActivity : BaseBarcodeReadableActivity(), MainItemViewAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    private var mAdapter: MainItemViewAdapter? = null

    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.setting -> SettingsActivity.createIntent(this@MainActivity)
            R.id.change_store -> {
                val fragment = StoreListDialogFragment.newInstance()
                fragment.isCancelable = false
                fragment.show(supportFragmentManager, "changeStore")
            }
            R.id.input_dummy_item -> onBarcodeInput("1234567890", BarcodePrefix.ITEM)
            R.id.input_dummy_store -> onBarcodeInput("1234567890", BarcodePrefix.STAFF)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    private val listener: MainActivityViewModel.Listener = object : MainActivityViewModel.Listener {
        override fun onNotReachableServer() {
            showNotReachableErrorDialog()
        }

        override fun onAddNewItem(item: Item) {
            mAdapter?.add(item)
        }

        override fun onClear() {
            mAdapter!!.clear()
        }

        override fun onAccount() {
            CalculatorDialogFragment.newInstance(mAdapter!!.sumPrice, mAdapter!!.data)
                    .show(supportFragmentManager, "Calculator")
        }

        override fun onChangeTitle(title: String) {
            binding.appBarLayout.toolbar.title = title
        }
    }

    override fun shouldEventSubscribes(): Boolean {
        return true
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

        viewModel.listener = listener
        binding.appBarLayout.contentMain.viewModel = viewModel

        mAdapter = MainItemViewAdapter(this)
        binding.appBarLayout.contentMain.recyclerView.adapter = mAdapter

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        binding.navView.menu.setGroupVisible(R.id.beta_test, Constants.TEST_MODE)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBinaryUpdate(event: BinaryUpdateEvent) {
        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSumPriceUpdate(event: SumPriceUpdateEvent) {
        viewModel.onSumPriceUpdate(event)
        if (mAdapter!!.itemCount > 0) {
            binding.appBarLayout.contentMain.recyclerView.smoothScrollToPosition(0)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessSentSale(event: SuccessSentSaleEvent) {
        mAdapter!!.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStoreUpdate(event: StoreUpdateEvent) {
        viewModel.onStoreUpdate(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStaffUpdate(event: StaffUpdateEvent) {
        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeState(event: ChangeStateEvent) {
        mAdapter!!.clear()
    }

    override fun onUpdateSumPrice(sumPrice: Int) {
        app!!.postEvent(SumPriceUpdateEvent(sumPrice))
    }

    /**
     * 読み取ったバーコードを用いてデータを取得する。
     * スタッフの登録作業が必要になる可能性がある
     *
     * @param barcode barcode
     * @param prefix  barcode type
     */
    override fun onBarcodeInput(barcode: String, prefix: BarcodePrefix) {
        viewModel.onBarcodeInput(barcode, prefix)
    }

    private fun showNotReachableErrorDialog() {
        AlertUtil.showErrorDialog(this,
                "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                false) { _, _ -> SettingsActivity.createIntent(this@MainActivity) }
    }

    companion object {
        fun createIntentWithClearTask(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
