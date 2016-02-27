package info.nukoneko.cuc.kidspos.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.OnClick;
import info.nukoneko.cuc.kidspos.R;
import info.nukoneko.cuc.kidspos.common.AsyncAPI;
import info.nukoneko.cuc.kidspos.common.AsyncAPICallback;
import info.nukoneko.cuc.kidspos.common.CommonActivity;
import info.nukoneko.cuc.kidspos.event.EventBusHolder;
import info.nukoneko.cuc.kidspos.event.EventItemAdapterChange;
import info.nukoneko.cuc.kidspos.itemlist.ItemListView;
import info.nukoneko.cuc.kidspos.navigation.NavigationAdapter;
import info.nukoneko.cuc.kidspos.navigation.NavigationItems;
import info.nukoneko.cuc.kidspos.navigation.NavigationView;
import info.nukoneko.cuc.kidspos.setting.SettingActivity;
import info.nukoneko.cuc.kidspos.util.KPToast;
import info.nukoneko.kidspos4j.model.DataBase;
import info.nukoneko.kidspos4j.model.ItemFactory;
import info.nukoneko.kidspos4j.model.ModelItem;

/**
 * created at 2015/06/13.
 */
public class TopPageActivity extends CommonActivity implements NavigationAdapter.OnItemClickListener {
    public static final int REQUEST_CODE_CALCULATE = 100;

    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigation;
    @Bind(R.id.item_list)
    ItemListView mItemListView;
    @Bind(R.id.price) TextView priceView;

    ActionBarDrawerToggle mDrawerToggle;

    private Integer sumPrice = 0;

    public Integer getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Integer sumPrice) {
        this.sumPrice = sumPrice;
        this.priceView.setText(String.valueOf(this.sumPrice));
    }

    @OnClick(R.id.account)
    public void onClickAccount(){
        CalculatorActivity.startActivity(this, REQUEST_CODE_CALCULATE,
                this.getSumPrice(),
                this.mItemListView.getAdapter().getItems());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        this.mNavigation.setOnItemClickListener(this);

        mDrawerToggle = getDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        ItemFactory.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
        DataBase<ModelItem> itemModel = ItemFactory.getInstance();
        ModelItem item = itemModel.find(String.format("barcode = '%s'", barcode)).get(0);
        if (item == null) {
            KPToast.showToast("登録されていない商品です");
        } else {
            this.mItemListView.getAdapter().add(item);
        }
    }

    private ActionBarDrawerToggle getDrawerToggle() {
        return new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_open,
                R.string.navigation_close);
    }

    @Override
    public void onItemClick(NavigationAdapter adapter, NavigationItems selectedItem) {
        switch (selectedItem){
            case SALES:
                break;

            case ITEMS:
                break;

            case STAFF:
                break;

            case SETTING:
                SettingActivity.startActivity(this);
                break;

            case UPDATE:
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                new AsyncAPI.Builder(this, new AsyncAPICallback() {
                    @Override
                    public Object doFunc(Object... params) {
                        update("http://localhost:8080/", "test.app");
                        return "";
                    }

                    @Override
                    public void onResult(Object result) {
                        System.exit(0);
                    }
                })
                        .setProgress(TopPageActivity.this, "アップデートしてます", false)
                        .build().run();
                break;

            case TEST_ADD_DUMMY: // dummy
                ModelItem dummyItem = new ModelItem();
                dummyItem.setName("ダミー");
                dummyItem.setPrice(300);
                this.mItemListView.getAdapter().add(dummyItem);
                break;
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
                this.mItemListView.getAdapter().clear();
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
}
