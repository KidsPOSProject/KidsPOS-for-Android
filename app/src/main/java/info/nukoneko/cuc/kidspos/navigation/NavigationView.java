package info.nukoneko.cuc.kidspos.navigation;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;

import info.nukoneko.cuc.kidspos.R;

/**
 * created at 2015/06/13.
 */
public class NavigationView extends RecyclerView {
    NavigationAdapter mAdapter = new NavigationAdapter();
    public NavigationView(Context context) {
        this(context, null);
    }
    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        addItemDecoration(new NavigationItemDecorator(getContext()));
        setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        setAdapter(mAdapter);
    }

    @Override
    public NavigationAdapter getAdapter(){
        return mAdapter;
    }

    public void setStoreName(String text){
        getAdapter().replaceItem(0, text);
    }

    public void setOnItemClickListener(NavigationAdapter.OnItemClickListener listener){
        this.mAdapter.setOnItemClickListener(listener);
    }
}
