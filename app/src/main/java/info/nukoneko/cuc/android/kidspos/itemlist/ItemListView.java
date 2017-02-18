package info.nukoneko.cuc.android.kidspos.itemlist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import info.nukoneko.cuc.android.kidspos.adapter.ItemListAdapter;

public class ItemListView extends RecyclerView {
    ItemListAdapter mAdapter = new ItemListAdapter();
    public ItemListView(Context context) {
        this(context, null, 0);
    }

    public ItemListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        addItemDecoration(new ItemListDecorator(getContext()));

        setHasFixedSize(false);
        LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        setAdapter(mAdapter);
    }

    @Override
    public ItemListAdapter getAdapter() {
        return (ItemListAdapter) super.getAdapter();
    }
}
