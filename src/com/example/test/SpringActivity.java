package com.example.test;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 富有弹性的ListView
 * 
 * @author eric liu
 *
 */
public class SpringActivity extends Activity {
	private SpringListView plv;
	private List<String> data;
	private PlvAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spring);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		plv = (SpringListView) findViewById(R.id.plv);
	}

	private void initListener() {
	}

	
	private void initData() {
		data = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			data.add("item " + i);
		}
		adapter = new PlvAdapter();
		plv.setAdapter(adapter);
	}

	@SuppressLint("ViewHolder")
	class PlvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size() > 0 ? data.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder h;
			if (convertView == null) {
				h = new ViewHolder();
				convertView = View.inflate(SpringActivity.this, R.layout.item_main_plv, null);
				h.content = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(h);
			} else {
				h = (ViewHolder) convertView.getTag();
			}
			h.content.setText(data.get(position));
			return convertView;
		}

		class ViewHolder {
			TextView content;
		}

	}

}
