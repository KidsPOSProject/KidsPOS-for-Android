package info.nukoneko.cuc.android.kidspos.ui.main;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemListItemBinding;
import info.nukoneko.cuc.android.kidspos.event.KPEventBusProvider;
import info.nukoneko.cuc.android.kidspos.event.obj.KPEventUpdateSumPrice;
import info.nukoneko.kidspos4j.model.ModelItem;

@SuppressWarnings("WeakerAccess")
public final class MainItemViewAdapter extends RecyclerView.Adapter<MainItemViewAdapter.ViewHolder> {
    public interface Listener {
        void onClickItem(@NonNull ModelItem item);
    }

    private final ArrayList<ModelItem> mData = new ArrayList<>();
    private final Context context;

    public MainItemViewAdapter(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ModelItem item = mData.get(position);
        if (item == null) return;
        holder.getBinding().setItem(item);
        holder.getBinding().getRoot().setOnClickListener(v -> {
            if (context instanceof Listener) ((Listener) context).onClickItem(item);
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
    public void add(@NonNull ModelItem item) {
        mData.add(0, item);
        notifyItemInserted(0);
        KPEventBusProvider.getInstance().send(new KPEventUpdateSumPrice(getSumPrice()));
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
        KPEventBusProvider.getInstance().send(new KPEventUpdateSumPrice(getSumPrice()));
    }

    public int getSumPrice() {
        int sum = 0;
        for (ModelItem item : mData) {
            sum += item.getPrice();
        }
        return sum;
    }

    public ArrayList<ModelItem> getData() {
        return mData;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final private ItemListItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public ItemListItemBinding getBinding() {
            return binding;
        }
    }
}
