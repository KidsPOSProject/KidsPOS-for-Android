package info.nukoneko.cuc.kidspos.navigation;

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
import info.nukoneko.cuc.kidspos.R;

/**
 * created at 2015/06/13.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private ArrayList<String> items = new ArrayList<>();
    public NavigationAdapter(ArrayList<String> items) {
        this.items.add("");
        this.items.addAll(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_navi_menu, parent, false);
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

    public void replaceItem(int position, String item){
        this.items.remove(position);
        this.items.add(position, item);
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
}
