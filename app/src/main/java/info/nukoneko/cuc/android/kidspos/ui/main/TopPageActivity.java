package info.nukoneko.cuc.android.kidspos.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import info.nukoneko.cuc.android.kidspos.AppController;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.StoreManager;
import info.nukoneko.cuc.android.kidspos.common.CommonActivity;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityTopBinding;
import info.nukoneko.cuc.android.kidspos.event.EventBusHolder;
import info.nukoneko.cuc.android.kidspos.event.EventItemAdapterChange;
import info.nukoneko.cuc.android.kidspos.navigation.NavigationAdapter;
import info.nukoneko.cuc.android.kidspos.navigation.NavigationItems;
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingActivity;
import info.nukoneko.kidspos4j.api.APIManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TopPageActivity extends CommonActivity
        implements NavigationAdapter.OnItemClickListener,
        TopPageActivityViewModel.Listener {
    public static final int REQUEST_CODE_CALCULATE = 100;

    ActivityTopBinding binding;
    TopPageActivityViewModel viewModel;

    ActionBarDrawerToggle mDrawerToggle;

    private Integer sumPrice = 0;

    public Integer getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Integer sumPrice) {
        this.sumPrice = sumPrice;
        binding.price.setText(String.valueOf(this.sumPrice));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_top);
        viewModel = new TopPageActivityViewModel();
        binding.setViewModel(viewModel);
        binding.setListener(this);

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        binding.navigationView.setOnItemClickListener(this);

        mDrawerToggle = getDrawerToggle();
        binding.drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        APIManager.Staff().getList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(modelSale -> {
                    Toast.makeText(this, "受信しました", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Toast.makeText(this, "送信に失敗しました", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onInputBarcode(String barcode) {
        // staff
        if (barcode.startsWith("1000")) {
            APIManager.Staff().getStaff(barcode)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(staff -> {
                        if (staff != null) {
                            AppController.get(this).getStoreManager().setCurrentStaff(staff);
                            binding.staffName.setText(staff.getName());
                        } else {
                            Toast.makeText(this, "正しく読み取りが行われませんでした", Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Toast.makeText(this, "何かがおかしいよ", Toast.LENGTH_SHORT).show();
                    });
        }

        /// item
        if (barcode.startsWith("1001")) {
             APIManager.Item().readItem(barcode)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item != null) {
                            binding.itemList.getAdapter().add(item);
                        } else {
                            Toast.makeText(this, "登録されていない商品です", Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Toast.makeText(this, "何かがおかしいよ", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private ActionBarDrawerToggle getDrawerToggle() {
        return new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                R.string.navigation_open,
                R.string.navigation_close);
    }

    @Override
    public void onItemClick(NavigationAdapter adapter, NavigationItems selectedItem) {
        switch (selectedItem){
//            case SALES:
//                break;
//
//            case ITEMS:
//
//                break;
//
//            case STAFF:
//                break;

            case SETTING:
                SettingActivity.startActivity(this);
                break;

//            case UPDATE:
//                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
//                new AsyncAPI.Builder(this, new AsyncAPICallback() {
//                    @Override
//                    public Object doFunc(Object... params) {
//                        update("http://localhost:8080/", "test.app");
//                        return "";
//                    }
//
//                    @Override
//                    public void onResult(Object result) {
//                        System.exit(0);
//                    }
//                })
//                        .setProgress(TopPageActivity.this, "アップデートしてます", false)
//                        .build().run();
//                break;
//
//            case TEST_ADD_DUMMY: // dummy
//                onInputBarcode("1001010003");
//                break;
//
//            case TEST_ADD_USER:
//                onInputBarcode("1000150001");
//                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBusHolder.EVENT_BUS.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusHolder.EVENT_BUS.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CALCULATE:
                binding.itemList.getAdapter().clear();
                this.setSumPrice(0);
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void OnItemAdapterChange(EventItemAdapterChange itemAdapterChange){
        setSumPrice(itemAdapterChange.getSum());
    }

    private void update(String apkUrl, String apkName) {
        try {
            URL url = new URL(apkUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH);
            if ( !file.exists()){
                file.mkdirs();
            }
            File outputFile = new File(file, apkName);
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH + apkName)), "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickAccount(View view) {
        CalculatorActivity.startActivity(this, REQUEST_CODE_CALCULATE,
                this.getSumPrice(),
                binding.itemList.getAdapter().getItems());
    }

    @Override
    public void onClickClear(View view) {
        binding.itemList.getAdapter().clear();
    }
}
