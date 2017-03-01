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
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateSubPrice;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.ModelItem;
import info.nukoneko.kidspos4j.model.ModelStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainActivityViewModel.Listener, MainItemViewAdapter.Listener {
    private ActivityMainBinding mBinding;
    private NavHeaderMainBinding mHeaderMainBinding;
    private MainActivityViewModel mViewModel = new MainActivityViewModel();
    private MainItemViewAdapter mAdapter;
    private boolean mIsDebugMode = true;

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
                    } else if (event instanceof KPEventUpdateSubPrice) {
                        mViewModel.setSumPrice(((KPEventUpdateSubPrice) event).getCurrentValue());
                    } else if (event instanceof KPEventSendFinish) {
                        mAdapter.clear();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ModelStore dummyStore = new ModelStore();
        dummyStore.setId(1);
        dummyStore.setName("ダミーのお店だよ");
        mViewModel.setCurrentStore(dummyStore);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickClear(View view) {
        onInputBarcode("1001000001", BARCODE_TYPE.ITEM);
    }


    @Override
    public void onClickAccount(View view) {
        CalculatorActivity.startActivity(this, 1000, mAdapter.getSumPrice(), mAdapter.getData());
    }

    @Override
    protected void onInputBarcode(@NonNull String barcode, BARCODE_TYPE type) {
        if (mIsDebugMode) {
            final ModelItem item = new ModelItem();
            item.setName("ダミー");
            item.setPrice(300);
            mAdapter.add(item);
        } else {
            APIManager.Item().readItem(barcode)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        mAdapter.add(item);
                    }, Throwable::printStackTrace);
        }
    }

    @Override
    public void onClickItem(@NonNull ModelItem item) {

    }
}
