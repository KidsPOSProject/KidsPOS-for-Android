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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import info.nukoneko.cuc.kidspos.R;
import info.nukoneko.cuc.kidspos.common.CommonActivity;
import info.nukoneko.cuc.kidspos.itemlist.ItemListView;
import info.nukoneko.cuc.kidspos.navigation.NavigationAdapter;
import info.nukoneko.cuc.kidspos.navigation.NavigationView;
import info.nukoneko.cuc.kidspos.util.AppUtils;
import info.nukoneko.cuc.kidspos.util.KPLogger;
import info.nukoneko.cuc.kidspos.util.KPToast;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.DataBase;
import info.nukoneko.kidspos4j.model.ItemFactory;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    public void onItemClick(NavigationAdapter adapter, int position, @StringRes int itemResID) {
        switch (position){
            case 1: // uriage
                break;

            case 2: // syouhin
                break;

            case 3: // employee
//                this.createJson();
                break;

            case 4: // dummy
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
        init();
        APIManager.Item().getList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<List<ModelItem>, Observable<ModelItem>>() {
                    @Override
                    public Observable<ModelItem> call(List<ModelItem> modelItems) {
                        return Observable.from(modelItems);
                    }
                })
                .subscribe(new Observer<ModelItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        KPLogger.d(e.toString());
                    }

                    @Override
                    public void onNext(ModelItem modelItem) {
                        mItemListView.getAdapter().add(modelItem);
                    }
                });


//        this.mItemListView.getAdapter().add(new ItemObject("だみー", 300));
    }
    public void init(){
        this.sumPrice = 0;
        this.priceView.setText("");
    }
}
