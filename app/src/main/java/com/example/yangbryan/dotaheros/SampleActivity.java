package com.example.yangbryan.dotaheros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;

import com.example.yangbryan.common.DataManager;


/**
 * 使用示例
 * 
 * @author savant-pan
 * 
 */
public class SampleActivity extends Activity {
	private ViewPagerIndicatorView viewPagerIndicatorView;
	private ListView listView1;
	private ListView listView2;
	private ListView listView3;
	private DataManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		dm = new DataManager(this.getApplicationContext());

		//set ViewPagerIndicatorView
		this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view);
		final TreeMap<String, View> map = new TreeMap<String, View>();
		listView1 = new ListView(this);
		SimpleAdapter sm1 =  new SimpleAdapter(this,getData("力量"), R.layout.activity_sample_pager_1,new String[]{"img","name_cn","position"},new int[]{R.id.img,R.id.name_cn,R.id.position});
		listView1.setAdapter(sm1);
		listView2 = new ListView(this);
		SimpleAdapter sm2 =  new SimpleAdapter(this,getData("敏捷"), R.layout.activity_sample_pager_1,new String[]{"img","name_cn","position"},new int[]{R.id.img,R.id.name_cn,R.id.position});
		listView2.setAdapter(sm2);
		listView3 = new ListView(this);
		listView1.setDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));
		listView1.setDividerHeight(2);
		listView2.setDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));
		listView2.setDividerHeight(2);
		listView3.setDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));
		listView3.setDividerHeight(2);
		SimpleAdapter sm3 =  new SimpleAdapter(this,getData("智力"), R.layout.activity_sample_pager_1,new String[]{"img","name_cn","position"},new int[]{R.id.img,R.id.name_cn,R.id.position});
		listView3.setAdapter(sm3);
		map.put("力量", listView1);
		map.put("敏捷", listView2);
		map.put("智力", listView3);
		this.viewPagerIndicatorView.setupLayout(map);
	}

	private List<Map<String, Object>> getData(String type) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		SQLiteDatabase db = dm.openDatabase("dota");
		Cursor c = db.rawQuery("SELECT * FROM heros where type = '" + type + "'", new String[]{});
		while (c.moveToNext()) {
			String name_cn = c.getString(c.getColumnIndex("name_cn"));
			String position = c.getString(c.getColumnIndex("position"));
			String img = c.getString(c.getColumnIndex("name_en"));
			int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

			map = new HashMap<String, Object>();
			map.put("img", imgId);
			map.put("name_cn", name_cn);
			map.put("position", position);
			list.add(map);
		}
		c.close();
		return list;
	}

}
