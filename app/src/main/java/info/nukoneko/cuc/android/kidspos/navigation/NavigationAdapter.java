package info.nukoneko.cuc.android.kidspos.navigation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import info.nukoneko.cuc.android.kidspos.AppController;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemNaviMenuBinding;

public class NavigationAdapter
        extends RecyclerView.Adapter<NavigationAdapter.ViewHolder>
        implements View.OnClickListener {

    private ArrayList<String> items = new ArrayList<>();

    private RecyclerView mParentView;

    private OnItemClickListener mListener;

    public NavigationAdapter() {
        this.items.add("");

        Context context = AppController.get().getApplicationContext();

        for (NavigationItems i : NavigationItems.values()){
            this.items.add(context.getString(i.val));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mParentView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mParentView = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_navi_menu, parent, false);
        itemView.setOnClickListener(this);
        itemView.setClickable(true);
        TypedValue outValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        itemView.setBackgroundResource(outValue.resourceId);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.getBinding().getRoot().setClickable(false);
            holder.getBinding().head.setVisibility(View.VISIBLE);
            holder.getBinding().menu.setVisibility(View.GONE);
            holder.getBinding().store.setText(items.get(position));
        } else {
            holder.getBinding().getRoot().setClickable(true);
            holder.getBinding().head.setVisibility(View.GONE);
            holder.getBinding().menu.setVisibility(View.VISIBLE);
            holder.getBinding().title.setText(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replaceItem(int position, @StringRes int itemResID){
        this.items.remove(position);
        this.items.add(position, AppController.get().getString(itemResID));
    }

    public void replaceItem(int position, String item){
        this.items.remove(position);
        this.items.add(position, item);
    }

    @Override
    public void onClick(View v) {

        if (this.mParentView == null) {
            return;
        }

        if (mListener != null) {
            int position = this.mParentView.getChildAdapterPosition(v);
            mListener.onItemClick(this, NavigationItems.getItem(position)); //this.items.get(position));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemNaviMenuBinding binding;

        ViewHolder(@NonNull View view) {
            super(view);
            binding = ItemNaviMenuBinding.bind(view);
        }

        public ItemNaviMenuBinding getBinding() {
            return binding;
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(NavigationAdapter adapter, NavigationItems selectedItem);
    }
}
