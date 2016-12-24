package info.nukoneko.cuc.android.kidspos.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.nukoneko.cuc.android.kidspos.R;
import info.nukoneko.kidspos4j.model.ModelItem;

/**
 * Created by atsumi on 2016/03/05.
 */
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
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item_name)
        TextView name;
        @Bind(R.id.item_price)
        TextView price;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
