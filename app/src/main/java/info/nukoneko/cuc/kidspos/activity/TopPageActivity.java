package info.nukoneko.cuc.kidspos.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.Subscribe;
import info.nukoneko.cuc.kidspos.R;
import info.nukoneko.cuc.kidspos.common.CommonActivity;
import info.nukoneko.cuc.kidspos.itemlist.ItemListView;
import info.nukoneko.cuc.kidspos.model.ItemObject;
import info.nukoneko.cuc.kidspos.model.SendAccountObject;
import info.nukoneko.cuc.kidspos.model.SendItemObject;
import info.nukoneko.cuc.kidspos.navigation.NavigationAdapter;
import info.nukoneko.cuc.kidspos.navigation.NavigationView;
import info.nukoneko.cuc.kidspos.observer.EventBusHolder;
import info.nukoneko.cuc.kidspos.observer.ReadItemEvent;
import info.nukoneko.cuc.kidspos.util.AppUtils;
import info.nukoneko.cuc.kidspos.util.KPLogger;
import info.nukoneko.cuc.kidspos.util.KPToast;
import info.nukoneko.cuc.kidspos.util.SQLiteManager;

/**
 * created at 2015/06/13.
 */
public class TopPageActivity extends CommonActivity implements NavigationAdapter.OnItemClickListener {
    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigation;
    @Bind(R.id.item_list)
    ItemListView mItemListView;
    @Bind(R.id.price) TextView priceView;

    @OnClick(R.id.account)
    public void onClickAccount(){
        Intent intent = new Intent(getApplicationContext(), CalculatorActivity.class);
        intent.putExtra(AppUtils.INTENT.EXTRA_VALUE, this.sumPrice);
        startActivity(intent);
    }

    ActionBarDrawerToggle mDrawerToggle;

    Integer sumPrice = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        this.mNavigation.setOnItemClickListener(this);

        mDrawerToggle = getDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

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
        ItemObject item = SQLiteManager.getItem(barcode);
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
    public void onItemClick(NavigationAdapter adapter, int position, @StringRes int itemResID) {
        switch (position){
            case 1: // uriage
                break;

            case 2: // syouhin
                break;

            case 3: // employee
                this.createJson();
                break;

            case 4: // dummy
                this.mItemListView.getAdapter().add(new ItemObject("だみー", 300));
                break;
        }
    }

    @Subscribe
    public void onAddItem(ReadItemEvent event){
        this.sumPrice += event.getItem().price;
        this.priceView.setText(String.valueOf(this.sumPrice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        EventBusHolder.EVENT_BUS.register(this);
        this.mItemListView.getAdapter().add(new ItemObject("だみー", 300));
    }

    @Override
    protected void onPause() {
        EventBusHolder.EVENT_BUS.unregister(this);
        super.onPause();
    }

    public void init(){
        this.sumPrice = 0;
        this.priceView.setText("");
    }

    public void createJson(){
        SendAccountObject account = new SendAccountObject(120, new Date().getTime());
        account.add(new SendItemObject("41204124124", 1));
        account.add(new SendItemObject("41203214000", 2));
        account.add(new SendItemObject("41203214001", 1));
        account.add(new SendItemObject("41203214003", 1));

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(account);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        KPLogger.d(json);

        Request request = new Request.Builder()
                .url("http://localhost:10800")
                .post(RequestBody.create(MediaType.parse("application/json"), json)).get().build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                KPLogger.d(response.body().string());
            }
        });

    }
}
