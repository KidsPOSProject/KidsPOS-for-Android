package info.nukoneko.cuc.android.kidspos.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.adapter.DBModelItemAdapter;
import info.nukoneko.cuc.android.kidspos.common.CommonActivity;
import info.nukoneko.cuc.android.kidspos.databinding.ActivityListItemBinding;
import info.nukoneko.kidspos4j.KidsPos4jConfig;
import info.nukoneko.kidspos4j.api.APIManager;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ItemListActivity extends CommonActivity {
    private DBModelItemAdapter mAdapter;

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, ItemListActivity.class));
    }

    ActivityListItemBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_item);

        mAdapter = new DBModelItemAdapter(this);

        binding.list.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadItemList(){

        KidsPos4jConfig.setDebug(true);

        List<ModelItem> list = APIManager.Item().getList(null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .flatMap(Observable::from).toList().toBlocking().first();

        if (list != null) {
//            for (ModelItem item : list){
//                mAdapter.add(item);
//            }
//            mAdapter.notifyDataSetChanged();
        }
    }
}
