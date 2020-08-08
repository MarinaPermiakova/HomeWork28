package com.example.android.homework28;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mySharedPreferences;
    private static String TEXT = "note_text";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Integer> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mySharedPreferences = getSharedPreferences(getString(R.string.large_text), MODE_PRIVATE);

        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        String s = String.valueOf(getText(R.string.large_text));
        myEditor.putString(TEXT, s);
        myEditor.apply();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView list = findViewById(R.id.list);

        final List<Map<String, String>> values = prepareContent();
        final BaseAdapter listContentAdapter = createAdapter(values);
        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                arrayList.add(position);
                values.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final List<Map<String, String>> values2 = prepareContent();
                final BaseAdapter listContentAdapter2 = createAdapter(values2);
                list.setAdapter(listContentAdapter2);
                mSwipeRefreshLayout.setRefreshing(false);
                listContentAdapter2.notifyDataSetChanged();
            }
        });

        arrayList = new ArrayList<>();
        if (savedInstanceState != null) {
            arrayList = savedInstanceState.getIntegerArrayList("indexes");
            final List<Map<String, String>> values2 = prepareContent();

            for (int i: arrayList) {
                values2.remove(i);
            }

            final BaseAdapter listContentAdapter2 = createAdapter(values2);
            list.setAdapter(listContentAdapter2);
            listContentAdapter2.notifyDataSetChanged();
        }
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this, values,
                R.layout.simple_adapter,
                new String[]{"text", "number"},
                new int[]{R.id.textView, R.id.textView2});
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, String> map;

        String[] arrayContent = mySharedPreferences.getString(TEXT, "").split("\n\n");
        for (String s : arrayContent) {
            map = new HashMap<>();
            map.put("text", s);
            map.put("number", String.valueOf(s.length()));
            arrayList.add(map);
        }
        return arrayList;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("indexes", arrayList);
    }
}