package com.example.yangbryan.dotaheros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.yangbryan.common.DataManager;


/**
 * 使用示例
 * 
 * @author Bryan Yang
 * 
 */
public class SampleActivity extends Activity {
	private ViewPagerIndicatorView viewPagerIndicatorView;
	private ArrayList<ListView> views = new ArrayList<ListView>();
	private String[] types = new String[]{"力量","敏捷","智力"};
	private DataManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		dm = new DataManager(this.getApplicationContext());

		for( int i=0;i <3;i++ ){
			ListView l = new ListView(this);

			l.setOnItemClickListener(onItemClickListener);
			SimpleAdapter sm =  new SimpleAdapter(this,getData(types[i]), R.layout.activity_sample_pager_1,
					new String[]{"img","name_cn","attack","position","camp","name2"},
					new int[]{R.id.img,R.id.name_cn,R.id.attack,R.id.position,R.id.camp,R.id.name2});
			l.setAdapter(sm);
			views.add(l);
		}
		//set ViewPagerIndicatorView
		this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view);
		final TreeMap<String, Object[]> map = new TreeMap<String, Object[]>();

		map.put("力量",new Object[]{R.drawable.overviewicon_str,views.get(0)});
		map.put("敏捷",new Object[]{R.drawable.overviewicon_agi,views.get(1)});
		map.put("智力",new Object[]{R.drawable.overviewicon_int,views.get(2)});


		this.viewPagerIndicatorView.setupLayout(map);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.viewPagerIndicatorView.tabIndicatorView.recyleStyle();
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
			String attack = c.getString(c.getColumnIndex("attack"));
			String camp = c.getString(c.getColumnIndex("camp"));
			String name2 = c.getString(c.getColumnIndex("name2"));
			int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

			map = new HashMap<String, Object>();
			map.put("img", imgId);
			map.put("name_cn", name_cn);
			map.put("attack",attack);
			map.put("position", position);
			map.put("camp",camp);
			map.put("name2",name2);
			list.add(map);
		}
		c.close();
		return list;
	}

	public AdapterView.OnItemClickListener onItemClickListener =  new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3){
			ListView v = (ListView)arg0;
			v.setBackgroundColor(getResources().getColor(R.color.black));
			Map<String, Object> s = (Map<String, Object>)v.getAdapter().getItem(pos);
			Integer imgId = (Integer)s.get("img");
			Resources res = getApplicationContext().getResources();
			String name_en = res.getResourceEntryName(imgId);
			//Toast.makeText(getApplicationContext(), name_en, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(SampleActivity.this, HeroDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name_en", name_en);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

}
