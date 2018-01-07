package info.nukoneko.cuc.android.kidspos.ui.main;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;

import info.nukoneko.cuc.android.kidspos.Constants;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding;
import info.nukoneko.cuc.android.kidspos.entity.Item;
import info.nukoneko.cuc.android.kidspos.entity.Staff;
import info.nukoneko.cuc.android.kidspos.event.BinaryUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.ChangeStateEvent;
import info.nukoneko.cuc.android.kidspos.event.StaffUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.StoreUpdateEvent;
import info.nukoneko.cuc.android.kidspos.event.SuccessSentSaleEvent;
import info.nukoneko.cuc.android.kidspos.event.SumPriceUpdateEvent;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity;
import info.nukoneko.cuc.android.kidspos.ui.main.calculate.CalculatorDialogFragment;
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListDialogFragment;
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingsActivity;
import info.nukoneko.cuc.android.kidspos.util.AlertUtil;
import info.nukoneko.cuc.android.kidspos.util.BarcodePrefix;
import info.nukoneko.cuc.android.kidspos.util.logger.LogFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MainActivity extends BaseBarcodeReadableActivity
        implements MainActivityViewModel.Listener, MainItemViewAdapter.Listener {

    private ActivityMainBinding mBinding;
    private MainActivityViewModel mViewModel = new MainActivityViewModel();
    private MainItemViewAdapter mAdapter;

    private final NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.setting:
                    SettingsActivity.createIntent(MainActivity.this);
                    break;
                case R.id.change_store:
                    final StoreListDialogFragment fragment = StoreListDialogFragment.newInstance();
                    fragment.setCancelable(false);
                    fragment.show(getSupportFragmentManager(), "changeStore");
                    break;
                case R.id.input_dummy_item:
                    onBarcodeInput("1234567890", BarcodePrefix.ITEM);
                    break;
                case R.id.input_dummy_store:
                    onBarcodeInput("1234567890", BarcodePrefix.STAFF);
                    break;
            }
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    @Override
    protected boolean isEventSubscribe() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.appBarLayout.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mBinding.appBarLayout.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mBinding.navView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mBinding.appBarLayout.contentMain.setViewModel(mViewModel);
        mBinding.appBarLayout.contentMain.setListener(this);

        mAdapter = new MainItemViewAdapter(this);
        mBinding.appBarLayout.contentMain.recyclerView.setAdapter(mAdapter);

        mViewModel.setCurrentStore(getApp().getCurrentStore());
        mViewModel.setCurrentStaff(getApp().getCurrentStaff());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle();
        if (!getApp().isPracticeModeEnabled()) {
            checkReachableServer();
        }
        mBinding.navView.getMenu().setGroupVisible(R.id.beta_test, Constants.TEST_MODE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBinaryUpdate(BinaryUpdateEvent event) {
        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSumPriceUpdate(SumPriceUpdateEvent event) {
        mViewModel.setSumPrice(event.getCurrentValue());
        if (mAdapter.getItemCount() > 0) {
            mBinding.appBarLayout.contentMain.recyclerView.smoothScrollToPosition(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessSentSale(SuccessSentSaleEvent event) {
        mAdapter.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStoreUpdate(StoreUpdateEvent event) {
        mViewModel.setCurrentStore(event.getStore());
        updateTitle();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffUpdate(StaffUpdateEvent event) {
        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeState(ChangeStateEvent event) {
        mAdapter.clear();
    }

    @Override
    public void onClickClear(View view) {
        mAdapter.clear();
    }

    @Override
    public void onClickAccount(View view) {
        CalculatorDialogFragment.newInstance(mAdapter.getSumPrice(), mAdapter.getData())
                .show(getSupportFragmentManager(), "Calculator");
    }

    @Override
    public void onClickItem(@NonNull Item item) {

    }

    @Override
    public void onUpdateSumPrice(int sumPrice) {
        getApp().postEvent(new SumPriceUpdateEvent(sumPrice));
    }

    /**
     * 読み取ったバーコードを用いてデータを取得する。
     * スタッフの登録作業が必要になる可能性がある
     *
     * @param barcode barcode
     * @param prefix  barcode type
     */
    @Override
    public void onBarcodeInput(@NonNull final String barcode, final BarcodePrefix prefix) {
        if (Constants.TEST_MODE) {
            Toast.makeText(this, String.format("%s", barcode), Toast.LENGTH_SHORT).show();
            switch (prefix) {
                case ITEM:
                    mAdapter.add(new Item(barcode));
                    break;
                case STAFF:
                    mViewModel.setCurrentStaff(new Staff(barcode));
                    break;
                default:
                    mAdapter.add(new Item(barcode));
                    mViewModel.setCurrentStaff(new Staff(barcode));
            }
            return;
        }

        // サーバから取得する
        switch (prefix) {
            case ITEM: {
                getApp().getApiService().readItem(barcode)
                        .enqueue(new Callback<Item>() {
                            @Override
                            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                                mAdapter.add(response.body());
                            }

                            @Override
                            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                                Toast.makeText(MainActivity.this, String.format("なにかがおかしいよ?\n%s", barcode), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            }
            case STAFF: {
                getApp().getApiService().getStaff(barcode)
                        .enqueue(new Callback<Staff>() {
                            @Override
                            public void onResponse(@NonNull Call<Staff> call, @NonNull Response<Staff> response) {
                                getApp().updateCurrentStaff(response.body());
                            }

                            @Override
                            public void onFailure(@NonNull Call<Staff> call, @NonNull Throwable t) {
                                Toast.makeText(MainActivity.this,
                                        "当日に登録したスタッフの場合、別途登録が必要です", Toast.LENGTH_SHORT).show();
                                getApp().updateCurrentStaff(new Staff(barcode));
                            }
                        });
                break;
            }
            case SALE:
                Toast.makeText(this, "レシートの読取は今はできません", Toast.LENGTH_SHORT).show();
                break;
            case UNKNOWN:
                break;
        }
    }

    private void updateTitle() {
        String title = getString(R.string.app_name);
        if (getApp().getCurrentStore() != null && getApp().getCurrentStore().isValid())
            title += String.format(" [%s]", getApp().getCurrentStore().getName());
        if (getApp().isPracticeModeEnabled()) title += " [練習モード]";
        if (Constants.TEST_MODE) title += " [テストモード]";

        mBinding.appBarLayout.toolbar.setTitle(title);
    }

    private void checkReachableServer() {
        new CheckReachableTask(this, getApp().getServerIp(), getApp().getServerPort());
    }

    private void showNotReachableErrorDialog() {
        AlertUtil.showErrorDialog(this,
                "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                false,
                (dialogInterface, i) -> SettingsActivity.createIntent(MainActivity.this));
    }

    private static final class CheckReachableTask extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<MainActivity> mActivity;

        private final String mServerIp;
        private final String mServerPort;

        CheckReachableTask(@NonNull MainActivity activity, @NonNull String serverIp, @NonNull String serverPort) {
            mActivity = new WeakReference<>(activity);

            mServerIp = serverIp;
            mServerPort = serverPort;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                final Socket sock = new Socket();
                sock.connect(new InetSocketAddress(mServerIp, Integer.parseInt(mServerPort)), 2000);
                sock.close();
                return true;
            } catch (Exception e) {
                Logger.e(e, "CheckReachableTask");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isReachable) {
            Logger.t(LogFilter.SERVER.name()).d("checkReachableServer %b", isReachable);
            if (isReachable) return;

            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.showNotReachableErrorDialog();
            }
        }
    }
}
