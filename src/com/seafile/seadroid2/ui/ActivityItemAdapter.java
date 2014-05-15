package com.seafile.seadroid2.ui;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seafile.seadroid2.BrowserActivity;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.ActivityItem;
import com.seafile.seadroid2.data.SeafActivity;
import com.seafile.seadroid2.data.SeafCachedFile;
import com.seafile.seadroid2.data.SeafGroup;

public class ActivityItemAdapter extends BaseAdapter {

	private ArrayList<ActivityItem> items;
	private BrowserActivity mActivity;

	public ActivityItemAdapter(BrowserActivity activity) {
		this.mActivity = activity;
		items = new ArrayList<ActivityItem>();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	public void addEntry(ActivityItem entry) {
		items.add(entry);
		// Collections.sort(items);
		notifyDataSetChanged();
	}

	public void add(SeafActivity activity) {
		items.add(activity);
	}

	public void notifyChanged() {
		notifyDataSetChanged();
	}

	@Override
	public ActivityItem getItem(int position) {
		return items.get(position);
	}

	public void setItem(ActivityItem item, int listviewPosition) {
		items.set(listviewPosition, item);
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		items.clear();
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnable(int position) {
		ActivityItem item = items.get(position);
		return !(item instanceof SeafGroup);
	}

	public boolean isClickable(int position) {
		return true;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public int getItemViewType(int position) {
		ActivityItem item = items.get(position);
		if (item instanceof SeafGroup)
			return 0;
		else
			return 1;
	}

	private View getActivityView(SeafActivity activity, View convertView,
			ViewGroup parent) {
		View view = convertView;
		Viewholder viewHolder;

		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(
					R.layout.list_item_entry, null);
			TextView title = (TextView) view.findViewById(R.id.list_item_title);
			TextView subtitle = (TextView) view
					.findViewById(R.id.list_item_subtitle);
			ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
			ImageView action = (ImageView) view
					.findViewById(R.id.list_item_action);
			viewHolder = new Viewholder(title, subtitle, icon, action);
			view.setTag(viewHolder);
		} else {
			viewHolder = (Viewholder) convertView.getTag();
		}

		viewHolder.title.setText(activity.getRepoName());
		viewHolder.subtitle.setText(activity.getDescription());
		viewHolder.icon.setImageResource(activity.getIcon());
		viewHolder.action.setVisibility(View.INVISIBLE);
		return view;
	}

	private View getCacheView(SeafCachedFile item, View convertView,
			ViewGroup parent) {
		View view = convertView;
		Viewholder viewHolder;

		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(
					R.layout.list_item_entry, null);
			TextView title = (TextView) view.findViewById(R.id.list_item_title);
			TextView subtitle = (TextView) view
					.findViewById(R.id.list_item_subtitle);
			ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
			ImageView action = (ImageView) view
					.findViewById(R.id.list_item_action);
			viewHolder = new Viewholder(title, subtitle, icon, action);
			view.setTag(viewHolder);
		} else {
			viewHolder = (Viewholder) convertView.getTag();
		}

		viewHolder.title.setText(item.getTitle());
		viewHolder.subtitle.setText(item.getSubtitle());
		viewHolder.icon.setImageResource(item.getIcon());
		viewHolder.action.setVisibility(View.INVISIBLE);
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ActivityItem item = items.get(position);
		if (item instanceof SeafActivity) {
			return getActivityView((SeafActivity) item, convertView, parent);
		} else if (item instanceof SeafCachedFile) {
			return getCacheView((SeafCachedFile) item, convertView, parent);
		} else {
			return getActivityView((SeafActivity) item, convertView, parent);
		}
	}

	private class Viewholder {
		TextView title, subtitle;
		ImageView icon, action;

		public Viewholder(TextView title, TextView subtitle, ImageView icon,
				ImageView action) {
			super();
			this.icon = icon;
			this.action = action;
			this.title = title;
			this.subtitle = subtitle;
		}
	}
}
