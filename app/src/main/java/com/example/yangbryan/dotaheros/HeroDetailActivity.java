package com.example.yangbryan.dotaheros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangbryan.common.DataManager;
/**
 * Created by yangg on 2015/10/11.
 */
public class HeroDetailActivity extends Activity  {
    private DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_detail);
        dm = new DataManager(this.getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name_en = bundle.getString("name_en");
        HashMap map = getHero(name_en);

        ImageView imageView = (ImageView)findViewById(R.id.hero_img);
        imageView.setImageDrawable(getResources().getDrawable((Integer) map.get("img")));

        TextView name_cn = (TextView)findViewById(R.id.hero_name_cn);
        name_cn.setText((String) map.get("name_cn"));

        TextView hero_story = (TextView)findViewById(R.id.hero_story);
        hero_story.setText((String) map.get("name2"));

        TextView hero_attack = (TextView)findViewById(R.id.hero_attack);
        hero_attack.setText((String)map.get("hero_attack"));

        TextView hero_camp = (TextView)findViewById(R.id.hero_camp);
        hero_camp.setText((String) map.get("hero_camp"));

        ListView l = (ListView)findViewById(R.id.skill_list);
        SimpleAdapter sm =  new SimpleAdapter(this,getSkills(name_en), R.layout.skill_list,
                new String[]{"img","name_cn","cost","colldown","skill_info"},
                new int[]{R.id.skill_img,R.id.skill_name,R.id.skill_cost,R.id.skill_cooldown,R.id.skill_info});
        l.setAdapter(sm);

    }


    private HashMap<String ,Object> getHero(String name_en){
        SQLiteDatabase db = dm.openDatabase("dota");
        HashMap map = new HashMap<String, Object>();
        Cursor c = db.rawQuery("SELECT * FROM heros where name_en = '" + name_en + "'", new String[]{});
        while (c.moveToNext()) {
            String name_cn = c.getString(c.getColumnIndex("name_cn"));
            String position = c.getString(c.getColumnIndex("position"));
            String img = c.getString(c.getColumnIndex("name_en"));
            String attack = c.getString(c.getColumnIndex("attack"));
            String camp = c.getString(c.getColumnIndex("camp"));
            String name2 = c.getString(c.getColumnIndex("name2"));
            int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

            map.put("img", imgId);
            map.put("name_cn", name_cn);
            map.put("attack",attack);
            map.put("position", position);
            map.put("camp",camp);
            map.put("name2",name2);

        }
        return map;
    }


    private List<Map<String, Object>> getSkills(String name_en) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        name_en = name_en.substring(0,name_en.length()-5)+"_";
        SQLiteDatabase db = dm.openDatabase("dota");
        Cursor c = db.rawQuery("SELECT * FROM skills where name_en like '" + name_en + "%'", new String[]{});
        while (c.moveToNext()) {
            String name_cn = c.getString(c.getColumnIndex("name_cn"));
            String cost = c.getString(c.getColumnIndex("cost"));
            String img = c.getString(c.getColumnIndex("name_en"));
            String colldown = c.getString(c.getColumnIndex("count_down"));
            String skill_info = c.getString(c.getColumnIndex("info"));
            int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

            map = new HashMap<String, Object>();
            map.put("img", imgId);
            map.put("name_cn", name_cn);
            map.put("cost",cost);
            map.put("colldown", colldown);
            map.put("skill_info", skill_info);
            list.add(map);
        }
        c.close();

        return list;
    }



}


