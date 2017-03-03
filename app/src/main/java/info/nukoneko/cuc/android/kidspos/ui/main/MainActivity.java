package info.nukoneko.cuc.android.kidspos.ui.main;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding;
import info.nukoneko.cuc.android.kidspos.databinding.NavHeaderMainBinding;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventAvailableUpdate;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventSendFinish;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateSumPrice;
import info.nukoneko.cuc.android.kidspos.ui.common.AlertUtil;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity;
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingsActivity;
import info.nukoneko.cuc.android.kidspos.util.KPPracticeTool;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class MainActivity extends BaseBarcodeReadableActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityViewModel.Listener, MainItemViewAdapter.Listener {
    private ActivityMainBinding mBinding;
    private NavHeaderMainBinding mHeaderMainBinding;
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

        mHeaderMainBinding = NavHeaderMainBinding.bind(mBinding.navView.getHeaderView(0));
        mHeaderMainBinding.setViewModel(mViewModel);
        mBinding.appBarLayout.contentMain.setViewModel(mViewModel);
        mBinding.appBarLayout.contentMain.setListener(this);

        mAdapter = new MainItemViewAdapter(this);
        mBinding.appBarLayout.contentMain.recyclerView.setAdapter(mAdapter);
        mBinding.appBarLayout.contentMain.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        KPEventBusProvider.getInstance().toObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    if (event instanceof KPEventAvailableUpdate) {
                        Toast.makeText(this, "アップデートが有効になりました", Toast.LENGTH_SHORT).show();
                    } else if (event instanceof KPEventUpdateSumPrice) {
                        mViewModel.setSumPrice(((KPEventUpdateSumPrice) event).getCurrentValue());
                    } else if (event instanceof KPEventSendFinish) {
                        mAdapter.clear();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.setCurrentStore(getApp().getCurrentStore());
        mViewModel.setCurrentStaff(getApp().getCurrentStaff());

        String title = getString(R.string.app_name);
        if (getApp().isPracticeModeEnabled()) title += "(練習モード)";
        if (getApp().isTestModeEnabled()) title += "(デバッグ中)";

        mBinding.appBarLayout.toolbar.setTitle(title);

        if (!getApp().isPracticeModeEnabled()) {
            getApp().checkServerReachable().subscribe(reachable -> {
                if (reachable) return;

                AlertUtil.showErrorDialog(this, "サーバーとの接続に失敗しました\nネットワーク接続を確認してください\n設定画面で設定を確認をしてください",
                        false,
                        (dialog, which) -> {
                            SettingsActivity.startActivity(this);
                        });
            });
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                SettingsActivity.startActivity(this);
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
        CalculatorActivity.startActivity(this, 1000, mAdapter.getSumPrice(), mAdapter.getData());
    }

    @Override
    public void onInputBarcode(@NonNull String barcode, BARCODE_TYPE type) {
        if (getApp().isTestModeEnabled()) {
            Toast.makeText(this, String.format("%s", barcode), Toast.LENGTH_SHORT).show();
        }
        if (getApp().isPracticeModeEnabled()) {
            mAdapter.add(KPPracticeTool.findModelItem(barcode));
        } else {
            APIManager.Item().readItem(barcode)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        mAdapter.add(item);
                    }, throwable -> {
                        Toast.makeText(this, String.format("なにかがおかしいよ?\n%s", barcode), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onClickItem(@NonNull ModelItem item) {

    }
}
