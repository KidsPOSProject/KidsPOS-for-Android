package info.nukoneko.cuc.android.kidspos.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemListItemBinding;
import info.nukoneko.cuc.android.kidspos.entity.Item;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.SumPriceUpdateEvent;

final class MainItemViewAdapter extends RecyclerView.Adapter<MainItemViewAdapter.ViewHolder> {
    public interface Listener {
        void onClickItem(@NonNull Item item);
    }

    private final List<Item> mData = new ArrayList<>();
    private final Context mContext;

    MainItemViewAdapter(@NonNull final Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = mData.get(position);
        if (item == null) return;
        holder.getBinding().setItem(item);
        holder.getBinding().getRoot().setOnClickListener(v -> {
            if (mContext instanceof Listener) ((Listener) mContext).onClickItem(item);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 先頭に代入します
     *
     * @param item 読み込んだ商品
     */
    void add(@NonNull Item item) {
        mData.add(0, item);
        notifyItemInserted(0);
        KPEventBusProvider.getInstance().send(new SumPriceUpdateEvent(getSumPrice()));
    }

    void clear() {
        mData.clear();
        notifyDataSetChanged();
        KPEventBusProvider.getInstance().send(new SumPriceUpdateEvent(getSumPrice()));
    }

    int getSumPrice() {
        int sum = 0;
        for (Item item : mData) {
            sum += item.getPrice();
        }
        return sum;
    }

    public List<Item> getData() {
        return mData;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        final private ItemListItemBinding mBinding;

        ViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }

        public ItemListItemBinding getBinding() {
            return mBinding;
        }
    }
}
