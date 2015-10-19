package com.example.yangbryan.dotaheros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangbryan.common.DataManager;

/**
 * Created by yangg on 2015/10/11.
 */
public class HeroDetailActivity extends Activity implements ViewPagerIndicatorView.OnUpdateViewListener {
    private DataManager dm;
    private Intent intent;
    private Bundle bundle;
    private String name_en;
    private LinearLayout skillList;
    private Handler handler_heroInfo;
    private Handler handler_heroSkills;
    private Handler handler_heroEquips;
    private Handler handler_heroHeros;
    private LayoutInflater inflater;
    private Map<String, Object> hero_map;
    private List<Map<String, Object>> skills;

    private ViewPagerIndicatorView viewPagerIndicatorView;
    private String[] tabs = new String[]{"来历", "技能", "出装", "相关"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_detail);
        dm = new DataManager(this.getApplicationContext());
        intent = getIntent();
        bundle = intent.getExtras();
        name_en = bundle.getString("name_en");
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view_1);
        final LinkedHashMap<String, Object[]> map = new LinkedHashMap<String, Object[]>();

        for (Integer i = 0; i < tabs.length; i++) {
            ScrollView s = new ScrollView(this);
            s.setId(0x10000+i);
            s.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
            map.put(tabs[i], new Object[]{R.drawable.overviewicon_str, s});
        }
        this.viewPagerIndicatorView.setupLayout(map);

        this.viewPagerIndicatorView.setUpdateViewListener(this);
        this.viewPagerIndicatorView.tabIndicatorView.changeStyle();
        initHandlers();
        //生成英雄
        new Thread(generateHeroInfo).start();
        //生成技能
        new Thread(generateSkill).start();


    }


    private HashMap<String, Object> getHero(String name_en) {
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
            String story = c.getString(c.getColumnIndex("story"));
            String equip_first = c.getString(c.getColumnIndex("equip_first"));
            String equip_low = c.getString(c.getColumnIndex("equip_low"));
            String equip_mid = c.getString(c.getColumnIndex("equip_mid"));
            String equip_heigh = c.getString(c.getColumnIndex("equip_heigh"));
            int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

            map.put("img", imgId);
            map.put("name_cn", name_cn);
            map.put("attack", attack);
            map.put("position", position);
            map.put("camp", camp);
            map.put("name2", name2);
            map.put("story", story);
            map.put("equip_0", equip_first);
            map.put("equip_1", equip_low);
            map.put("equip_2", equip_mid);
            map.put("equip_3", equip_heigh);


        }
        return map;
    }

    private List<Map<String, Object>> getSkills(String name_en) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        name_en = name_en.substring(0, name_en.length() - 5) + "_";
        SQLiteDatabase db = dm.openDatabase("dota");
        Cursor c = db.rawQuery("SELECT * FROM skills where name_en like '" + name_en + "%'", new String[]{});
        while (c.moveToNext()) {
            String name_cn = c.getString(c.getColumnIndex("name_cn"));
            String cost = c.getString(c.getColumnIndex("cost"));
            String img = c.getString(c.getColumnIndex("name_en"));
            String colldown = c.getString(c.getColumnIndex("count_down"));
            String color_green = c.getString(c.getColumnIndex("color_green"));
            String skill_info = c.getString(c.getColumnIndex("info"));
            String otherInfos = c.getString(c.getColumnIndex("otherInfos"));
            int imgId = getResources().getIdentifier(img, "drawable", getPackageName());

            map = new HashMap<String, Object>();
            map.put("img", imgId);
            map.put("name_cn", name_cn);
            map.put("cost", cost);
            map.put("cooldown", colldown);
            map.put("color_green", color_green);
            map.put("skill_info", skill_info);
            map.put("otherInfos", otherInfos);
            list.add(map);
        }
        c.close();

        return list;
    }

    private View.OnClickListener skillTitleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (skillList.getLayoutParams().height == 0) {
                skillList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
            } else {
                skillList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0));
            }
        }
    };

    @Override
    public void updateView(int pos) {
        ScrollView s = (ScrollView)viewPagerIndicatorView.viewPager.findViewById(0x10000 + pos);
        if(s == null) return;
        s.removeAllViews();
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        view.setOrientation(LinearLayout.VERTICAL);
        if(pos == 0){
            Map<String, Object> map = hero_map;
            View v  = inflater.inflate(R.layout.hero_story, null);
            ((TextView)v.findViewById(R.id.hero_story)).setText((String) map.get("story"));
            view.addView(v);
            s.addView(view);
        }
        else if(pos == 1){
            for (Integer i = 0; i < skills.size(); i++) {
                View skillView = inflater.inflate(R.layout.skill_list, null);
                TextView name = (TextView) skillView.findViewById(R.id.skill_name);
                name.setText((String) skills.get(i).get("name_cn"));
                TextView info = (TextView) skillView.findViewById(R.id.skill_info);
                info.setText((String) skills.get(i).get("skill_info"));
                TextView cost = (TextView) skillView.findViewById(R.id.skill_cost);
                cost.setText((String) skills.get(i).get("cost"));
                TextView cd = (TextView) skillView.findViewById(R.id.skill_cooldown);
                cd.setText((String) skills.get(i).get("cooldown"));
                TextView cg = (TextView) skillView.findViewById(R.id.skill_color_green);
                cg.setText((String) skills.get(i).get("color_green"));
                if (i == skills.size() - 1) {
                    View divider = skillView.findViewById(R.id.skill_divider);
                    divider.setVisibility(View.GONE);
                }

                ImageView img = (ImageView) skillView.findViewById(R.id.skill_img);
                img.setImageResource((Integer) skills.get(i).get("img"));

                //生成skill的其他信息
                TableLayout skillDoc = (TableLayout) skillView.findViewById(R.id.skillTable);
                String[] others = ((String) skills.get(i).get("otherInfos")).split("\\|");
                for (Integer j = 0; j < others.length; j = j + 2) {
                    TableRow row = new TableRow(getApplicationContext());
                    TextView t1 = new TextView(getApplicationContext());
                    t1.setText(others[j]);
                    row.addView(t1);
                    if (j + 1 < others.length) {
                        TextView t2 = new TextView(getApplicationContext());
                        t2.setText(others[j + 1]);
                        row.addView(t2);
                    }
                    skillDoc.addView(row);
                }
                view.addView(skillView);
            }
            s.addView(view);
        }
        else if (pos == 2) {
            Map<String, Object> map = hero_map;
            for (Integer i = 0; i < 4; i++) {
                String equip = (String) map.get("equip_" + i);
                if (equip != "") {
                    GridView gv = new GridView(getApplicationContext());
                    gv.setPadding(10,10,10,10);
                    List<Map<String, Object>> listItmes = new ArrayList<Map<String, Object>>();
                    String[] strs = equip.split(",");
                    for (Integer j = 0; j < strs.length; j++) {
                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("image", getResources().getIdentifier(strs[j], "drawable", getPackageName()));
                        m.put("name_en", strs[j]);
                        listItmes.add(m);
                    }
                    SimpleAdapter sm = new SimpleAdapter(getApplicationContext(),
                            listItmes,
                            R.layout.equip_list,
                            new String[]{"name_en", "image"},
                            new int[]{R.id.equip_name_en, R.id.equip_img});
                    gv.setAdapter(sm);
                    gv.setNumColumns(5);
                    gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                    gv.setColumnWidth(5);
                    gv.setOnItemClickListener(onItemClick);
                    TextView t = new TextView(getApplicationContext());
                    t.setTextSize(13);
                    String[] sss = new String[]{"出门装备", "游戏初期", "核心装备", "后期神装"};
                    t.setText(sss[i]);
                    t.setTextSize(10);
                    view.addView(t);
                    view.addView(gv);
                    if (i == 3) {

                    } else {
                        view.addView(inflater.inflate(R.layout.divider_2, null));
                    }
                }
            }
            s.addView(view);
        }

    }

    private GridView.OnItemClickListener onItemClick = new GridView.OnItemClickListener(){
        @Override
        public  void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
            Intent intent = new Intent(HeroDetailActivity.this, Equip.class);
            Bundle bundle = new Bundle();
            bundle.putString("name_en", "a");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };




    private void initHandlers() {
        handler_heroInfo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //更新UI
                SerializableMap _map = (SerializableMap) msg.getData().get("map");
                hero_map = _map.getMap();
                ImageView imageView = (ImageView) findViewById(R.id.hero_img);
                String name_en_ = name_en.substring(0, name_en.length() - 5) + "_full";
                int imgId = getResources().getIdentifier(name_en_, "drawable", getPackageName());
                if (imgId == 0) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_1));
                }
                imageView.setImageResource(imgId);

                TextView name_cn = (TextView) findViewById(R.id.hero_name_cn);
                name_cn.setText((String) hero_map.get("name_cn"));

                TextView hero_attack = (TextView) findViewById(R.id.hero_attack);
                hero_attack.setText((String) hero_map.get("attack"));
                TextView hero_camp = (TextView) findViewById(R.id.hero_camp);
                hero_camp.setText((String) hero_map.get("camp"));
                super.handleMessage(msg);
            }
        };

        handler_heroSkills = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SerializableList _skills = (SerializableList) msg.getData().get("list");
                skills = _skills.getList();
                super.handleMessage(msg);
            }
        };
    }

    private Runnable generateHeroInfo = new Runnable() {
        @Override
        public void run() {
            HashMap map = getHero(name_en);
            Message m = handler_heroInfo.obtainMessage();
            Bundle bundle = new Bundle();
            final SerializableMap myMap = new SerializableMap();
            myMap.setMap(map);
            bundle.putSerializable("map", myMap);
            m.setData(bundle);
            handler_heroInfo.sendMessage(m);
        }
    };

    private Runnable generateSkill = new Runnable() {
        @Override
        public void run() {
            List<Map<String, Object>> skills = getSkills(name_en);
            Message m = handler_heroInfo.obtainMessage();
            Bundle bundle = new Bundle();
            final SerializableList myList = new SerializableList();
            myList.setList(skills);
            bundle.putSerializable("list", myList);
            m.setData(bundle);
            handler_heroSkills.sendMessage(m);

        }
    };


    /**
     * 序列化map供Bundle传递map使用
     * Created  on 13-12-9.
     */
    public class SerializableMap implements Serializable {

        private Map<String, Object> map;

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }
    }

    public class SerializableList implements Serializable {

        private List<Map<String, Object>> list;

        public List<Map<String, Object>> getList() {
            return list;
        }

        public void setList(List<Map<String, Object>> map) {
            this.list = map;
        }
    }

}




