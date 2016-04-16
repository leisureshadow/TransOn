package com.ntu.transon.history;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntu.transon.R;

public class HistorySearchItemAdapter extends ArrayAdapter<HistorySearchItem> {
	

	private int resource;

	private List<HistorySearchItem> items;
	
	public HistorySearchItemAdapter(Context context, int resource, List<HistorySearchItem> items) {
		super(context, resource, items);
		this.resource = resource;
		this.items = items;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView;

		final HistorySearchItem item = getItem(position);
		
		if (convertView == null) {

			itemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)
					getContext().getSystemService(inflater);
			li.inflate(resource, itemView, true);
		}
		else {
			itemView = (LinearLayout) convertView;
		}
		

//		RelativeLayout typeColor = (RelativeLayout) itemView.findViewById(R.id.type_color);
//		ImageView selectedItem = (ImageView) itemView.findViewById(R.id.selected_item);
		TextView titleView = (TextView) itemView.findViewById(R.id.title_text);
		TextView idView = (TextView) itemView.findViewById(R.id.history_id);
        TextView locationView = (TextView) itemView.findViewById(R.id.history_location);
		

//		GradientDrawable background = (GradientDrawable)typeColor.getBackground();
//		background.setColor(item.getColor().parseColor());
		

		titleView.setText(item.getSubject());
        idView.setText(item.getId());
        locationView.setText(item.getlocation());
		

//		selectedItem.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
		
		return itemView;
	}
	

	public void set(int index, HistorySearchItem item) {
		if (index >= 0 && index < items.size()) {
			items.set(index, item);
			notifyDataSetChanged();
		}
	}
	

	public HistorySearchItem get(int index) {
		return items.get(index);
	}

}
