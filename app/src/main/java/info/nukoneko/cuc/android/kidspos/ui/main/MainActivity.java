package info.nukoneko.cuc.android.kidspos.ui.main;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventAvailableUpdate;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventSendFinish;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateStaff;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateStore;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateSumPrice;
import info.nukoneko.cuc.android.kidspos.ui.calculator.CalculatorActivity;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity;
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingsActivity;
import info.nukoneko.cuc.android.kidspos.util.KPPracticeTool;
import info.nukoneko.cuc.android.kidspos.util.rx.RxWrap;
import info.nukoneko.cuc.kidspos4j.api.APIManager;
import info.nukoneko.cuc.kidspos4j.util.config.BarcodeRule;
import rx.android.schedulers.AndroidSchedulers;

@SuppressWarnings("FieldCanBeLocal")
public final class MainActivity extends BaseBarcodeReadableActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityViewModel.Listener {
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

        KPEventBusProvider.getInstance().toObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    if (event instanceof KPEventAvailableUpdate) {
                        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show();
                    } else if (event instanceof KPEventUpdateSumPrice) {
                        mViewModel.setSumPrice(((KPEventUpdateSumPrice) event).getCurrentValue());
                        if (mAdapter.getItemCount() > 0) {
                            mBinding.appBarLayout.contentMain.recyclerView.smoothScrollToPosition(0);
                        }
                    } else if (event instanceof KPEventSendFinish) {
                        mAdapter.clear();
                    } else if (event instanceof KPEventUpdateStore) {
                        mViewModel.setCurrentStore(((KPEventUpdateStore) event).getStore());
                        updateTitle();
                    } else if (event instanceof KPEventUpdateStaff) {
                        mViewModel.setCurrentStaff(((KPEventUpdateStaff) event).getStaff());
                        updateTitle();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTitle();

        if (!getApp().isPracticeModeEnabled()) {
            getApp().checkServerReachable().subscribe(reachable -> {
                if (reachable) return;

                AlertUtil.showErrorDialog(this, "サーバーとの接続に失敗しました\n・ネットワーク接続を確認してください\n・設定画面で設定を確認をしてください",
                        false,
                        (dialog, which) -> {
                            SettingsActivity.startActivity(this);
                        });
            });
        }
    }

    private void updateTitle() {
        String title = getString(R.string.app_name);
        if (getApp().getCurrentStore() != null) title += String.format(" [%s]", getApp().getCurrentStore().getName());
        if (getApp().isPracticeModeEnabled()) title += " [練習モード]";
        if (getApp().isTestModeEnabled()) title += " [デバッグ中]";

        mBinding.appBarLayout.toolbar.setTitle(title);
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

    /**
     * 読み取ったバーコードを用いてデータを取得する。
     * スタッフの登録作業が必要になる可能性がある
     * @param barcode barcode
     * @param type barcode type
     */
    @Override
    public void onInputBarcode(@NonNull String barcode, BarcodeRule.BARCODE_PREFIX type) {
        if (getApp().isTestModeEnabled()) {
            Toast.makeText(this, String.format("%s", barcode), Toast.LENGTH_SHORT).show();
            if (type == BarcodeRule.BARCODE_PREFIX.UNKNOWN) {
                mAdapter.add(KPPracticeTool.findModelItem(barcode));
                mViewModel.setCurrentStaff(KPPracticeTool.findModelStaff(barcode));
                return;
            }
        }
        if (getApp().isPracticeModeEnabled()) {
            // 練習モードが有効な場合、データはアプリ内から取得する
            switch (type) {
                case ITEM:
                    mAdapter.add(KPPracticeTool.findModelItem(barcode));
                    break;
                case STAFF:
                    mViewModel.setCurrentStaff(KPPracticeTool.findModelStaff(barcode));
                    break;
                case UNKNOWN:
                    mAdapter.add(KPPracticeTool.findModelItem(barcode));
                    break;
            }
        } else {
            // サーバから取得する
            switch (type) {
                case ITEM:
                    RxWrap.create(APIManager.Item().readItem(barcode), bindToLifecycle())
                            .subscribe(item -> {
                                mAdapter.add(item);
                            }, throwable -> {
                                Toast.makeText(this, String.format("なにかがおかしいよ?\n%s", barcode), Toast.LENGTH_SHORT).show();
                            });
                    break;
                case STAFF:
                    RxWrap.create(APIManager.Staff().getStaff(barcode), bindToLifecycle())
                            .subscribe(modelStaff -> {
                                getApp().updateCurrentStaff(modelStaff);
                            }, throwable -> {
                                AlertUtil.showAlert(this, "登録されてないスタッフ", "当日に登録したスタッフの場合、別途登録が必要です");
                            });
                    break;
                case SALE:
                    Toast.makeText(this, "レシートの読取は今はできません", Toast.LENGTH_SHORT).show();
                    break;
                case UNKNOWN:
                    break;
            }
        }
    }
}
