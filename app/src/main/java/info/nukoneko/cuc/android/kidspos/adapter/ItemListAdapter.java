package info.nukoneko.cuc.android.kidspos.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemItemMenuBinding;
import info.nukoneko.cuc.android.kidspos.event.EventBusHolder;
import info.nukoneko.cuc.android.kidspos.event.EventItemAdapterChange;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.Observable;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private ArrayList<ModelItem> items = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_item_menu, parent, false);
        itemView.setClickable(true);
        TypedValue outValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        itemView.setBackgroundResource(outValue.resourceId);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.price
                .setText(String.valueOf(this.items.get(position).getPrice()));
        holder.binding.name
                .setText(this.items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<ModelItem> getItems() {
        return items;
    }

    public void add(ModelItem object){
        this.items.add(object);
        notifyDataChanged();
    }

    public void clear(){
        this.items.clear();
        notifyDataChanged();
    }

    private void notifyDataChanged(){
        int sum = 0;
        if (this.items.size() > 0) {
            ModelItem[] modelItems = new ModelItem[this.items.size()];
            sum = Observable.from(this.items.toArray(modelItems))
                    .map(ModelItem::getPrice)
                    .reduce((integer, integer2) -> integer + integer2)
                    .toBlocking().first();
        }
        EventBusHolder.EVENT_BUS.post(new EventItemAdapterChange(sum, this.items.size()));
        this.notifyDataSetChanged();
    }

    public void insert(ModelItem object){
        this.items.add(0, object);
        notifyDataChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemItemMenuBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemItemMenuBinding.bind(view);
        }

        public ItemItemMenuBinding getBinding() {
            return binding;
        }
    }
}
