package info.nukoneko.cuc.android.kidspos.ui.main;

import android.content.DialogInterface;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetSocketAddress;
import java.net.Socket;

import info.nukoneko.cuc.android.kidspos.KidsPOSApplication;
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
import info.nukoneko.cuc.android.kidspos.ui.calculator.CalculatorActivity;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity;
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingsActivity;
import info.nukoneko.cuc.android.kidspos.util.BarcodePrefix;
import info.nukoneko.cuc.android.kidspos.util.KidsPOSLogger;
import info.nukoneko.cuc.android.kidspos.util.LogFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MainActivity extends BaseBarcodeReadableActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityViewModel.Listener, MainItemViewAdapter.Listener {
    private ActivityMainBinding mBinding;
    private MainActivityViewModel mViewModel = new MainActivityViewModel();
    private MainItemViewAdapter mAdapter;

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
        mBinding.navView.setNavigationItemSelectedListener(this);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                SettingsActivity.startActivity(this);
                break;
            case R.id.change_store:
                final StoreListDialogFragment fragment = StoreListDialogFragment.newInstance();
                fragment.setCancelable(false);
                fragment.show(getSupportFragmentManager(), "changeStore");
                break;
        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickClear(View view) {
        mAdapter.clear();
    }

    @Override
    public void onClickAccount(View view) {
        CalculatorActivity.startActivity(this, mAdapter.getSumPrice(), mAdapter.getData());
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
    public void onInputBarcode(@NonNull final String barcode, final BarcodePrefix prefix) {
        if (getApp().isTestModeEnabled()) {
            Toast.makeText(this, String.format("%s", barcode), Toast.LENGTH_SHORT).show();
            if (prefix == BarcodePrefix.UNKNOWN) {
                mAdapter.add(new Item(barcode));
                mViewModel.setCurrentStaff(new Staff(barcode));
                return;
            }
        }

        // サーバから取得する
        switch (prefix) {
            case ITEM: {
                getApp().getApiService().readItem(barcode)
                        .enqueue(new Callback<Item>() {
                            @Override
                            public void onResponse(Call<Item> call, Response<Item> response) {
                                mAdapter.add(response.body());
                            }

                            @Override
                            public void onFailure(Call<Item> call, Throwable t) {
                                Toast.makeText(MainActivity.this, String.format("なにかがおかしいよ?\n%s", barcode), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            }
            case STAFF: {
                getApp().getApiService().getStaff(barcode)
                        .enqueue(new Callback<Staff>() {
                            @Override
                            public void onResponse(Call<Staff> call, Response<Staff> response) {
                                getApp().updateCurrentStaff(response.body());
                            }

                            @Override
                            public void onFailure(Call<Staff> call, Throwable t) {
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
        if (getApp().isTestModeEnabled()) title += " [デバッグ中]";

        mBinding.appBarLayout.toolbar.setTitle(title);
    }

    private void checkReachableServer() {
        final KidsPOSApplication app = getApp();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    final Socket sock = new Socket();
                    sock.connect(new InetSocketAddress(app.getServerIp(), Integer.parseInt(app.getServerPort())), 2000);
                    sock.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isReachable) {
                KidsPOSLogger.d(LogFilter.SERVER, "checkReachableServer %b", isReachable);
                if (isReachable) return;

                AlertUtil.showErrorDialog(MainActivity.this,
                        "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                        false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SettingsActivity.startActivity(MainActivity.this);
                            }
                        });
            }
        }.execute();
    }
}
