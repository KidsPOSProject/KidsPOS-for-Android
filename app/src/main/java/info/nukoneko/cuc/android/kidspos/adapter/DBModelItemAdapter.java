package info.nukoneko.cuc.android.kidspos.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.cuc.android.kidspos.databinding.ItemDbItemListBinding;
import info.nukoneko.kidspos4j.model.ModelItem;

public class DBModelItemAdapter extends ArrayAdapter<ModelItem>{

    private Context context;
    private LayoutInflater mInflater;

    public DBModelItemAdapter(Context context) {
        super(context, android.R.layout.activity_list_item);
        mInflater = (LayoutInflater) context.getSystemService((Activity.LAYOUT_INFLATER_SERVICE));
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_db_item_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ModelItem item = getItem(position);
        holder.binding.itemName.setText(item.getName());
        holder.binding.itemPrice.setText(item.getPrice());
        return convertView;
    }

    static class ViewHolder {
        private ItemDbItemListBinding binding;

        ViewHolder(View view){
            binding = ItemDbItemListBinding.bind(view);
        }

        public ItemDbItemListBinding getBinding() {
            return binding;
        }
    }
}
