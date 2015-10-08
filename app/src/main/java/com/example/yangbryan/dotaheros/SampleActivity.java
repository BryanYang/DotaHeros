package com.example.yangbryan.dotaheros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;

import com.example.yangbryan.dotaheros.R;
import com.example.yangbryan.dotaheros.ViewPagerIndicatorView;

/**
 * 使用示例
 * 
 * @author savant-pan
 * 
 */
public class SampleActivity extends Activity {
	private ViewPagerIndicatorView viewPagerIndicatorView;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		SQLiteDatabase db = openOrCreateDatabase("heros", Context.MODE_PRIVATE, null);
		//set ViewPagerIndicatorView
		this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view);
		final TreeMap<String, View> map = new TreeMap<String, View>();
		listView = new ListView(this);
		SimpleAdapter sm =  new SimpleAdapter(this,getData(), R.layout.activity_sample_pager_1,new String[]{"img","name","dingwei"},new int[]{R.id.img,R.id.name,R.id.dingwei});
		listView.setAdapter(sm);
		map.put("力量", LayoutInflater.from(this).inflate(R.layout.activity_sample_pager_0, null));
		map.put("敏捷", listView);
		map.put("智力", LayoutInflater.from(this).inflate(R.layout.activity_sample_pager_2, null));
		this.viewPagerIndicatorView.setupLayout(map);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		SQLiteDatabase db = openOrCreateDatabase("yang.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT * FROM heros", new String[]{});
		while (c.moveToNext()) {
			int _id = c.getInt(c.getColumnIndex("id"));
			String name = c.getString(c.getColumnIndex("name"));
			String dingwei = c.getString(c.getColumnIndex("dingwei"));
			String info = c.getString(c.getColumnIndex("info"));
			String img = c.getString(c.getColumnIndex("img"));
			int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

			map = new HashMap<String, Object>();
			map.put("img", R.drawable.earthshaker_vert);
			map.put("name", name);
			map.put("dingwei", dingwei);
			list.add(map);
		}
		c.close();

		return list;
	}

}
