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

final class MainItemViewAdapter extends RecyclerView.Adapter<MainItemViewAdapter.ViewHolder> {
    public interface Listener {
        void onClickItem(@NonNull Item item);
        void onUpdateSumPrice(int sumPrice);
    }

    private final List<Item> mData = new ArrayList<>();
    private final Context mContext;
    private final Listener mListener;

    <T extends Context & Listener> MainItemViewAdapter(@NonNull final T context) {
        mContext = context;
        mListener = context;
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
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickItem(item);
            }
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
        mListener.onUpdateSumPrice(getSumPrice());
    }

    void clear() {
        mData.clear();
        notifyDataSetChanged();
        mListener.onUpdateSumPrice(getSumPrice());
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
