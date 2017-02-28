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

import info.nukoneko.cuc.android.kidspos.BR;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding;
import info.nukoneko.cuc.android.kidspos.databinding.NavHeaderMainBinding;
import info.nukoneko.cuc.android.kidspos.ui.common.BaseActivity;
import info.nukoneko.kidspos4j.model.ModelStore;

public final class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityViewModel.Listener {
    private ActivityMainBinding binding;
    private NavHeaderMainBinding headerMainBinding;
    private MainActivityViewModel viewModel = new MainActivityViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.appBarLayout.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarLayout.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        headerMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        headerMainBinding.setViewModel(viewModel);
        binding.appBarLayout.contentMain.setVariable(BR.viewModel, viewModel);
        binding.appBarLayout.contentMain.setVariable(BR.listener, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ModelStore dummyStore = new ModelStore();
        dummyStore.setId(1);
        dummyStore.setName("ダミーのお店だよ");
        viewModel.setCurrentStore(dummyStore);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickClear(View view) {
        Toast.makeText(this, "けすよ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickAccount(View view) {
        Toast.makeText(this, "かいけいするよ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onInputBarcode(@NonNull String barcode, BARCODE_TYPE type) {
        
    }
}
