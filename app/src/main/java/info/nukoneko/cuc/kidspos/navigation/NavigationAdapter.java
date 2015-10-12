package info.nukoneko.cuc.kidspos.navigation;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;
import info.nukoneko.cuc.kidspos.AppController;
import info.nukoneko.cuc.kidspos.R;

/**
 * created at 2015/06/13.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<String> items = new ArrayList<>();

    private RecyclerView mParentView;

    private OnItemClickListener mListener;

    public NavigationAdapter(ArrayList<Integer> items) {
        this.items.add("");

        Context context = AppController.get().getApplicationContext();

        for (Integer i : items){
            this.items.add(context.getString(i));
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
            holder.getView().setClickable(false);
            holder.headView.setVisibility(View.VISIBLE);
            holder.menuView.setVisibility(View.GONE);
            holder.storeName.setText(items.get(position));
        } else {
            holder.getView().setClickable(true);
            holder.headView.setVisibility(View.GONE);
            holder.menuView.setVisibility(View.VISIBLE);
            holder.itemName.setText(items.get(position));
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
            mListener.onItemClick(this, position, R.string.app_name); //this.items.get(position));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.head) LinearLayout headView;
        @Bind(R.id.store) TextView storeName;

        // TODO いつかわける。
        @Bind(R.id.menu) LinearLayout menuView;
        @Bind(R.id.title) TextView itemName;

        public View v;
        public ViewHolder(View view) {
            super(view);
            v = view;
            ButterKnife.bind(this, view);
        }

        public View getView(){
            return v;
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(NavigationAdapter adapter, int position, @StringRes int itemResID);
    }
}
