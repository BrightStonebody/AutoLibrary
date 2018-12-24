package com.hsm.zzh.cl.autolibrary.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;


import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.adapter.HistoryAdapter;
import com.hsm.zzh.cl.autolibrary.adapter.SpaceItemDecoration;
import com.hsm.zzh.cl.autolibrary.info_api.Book;
import com.hsm.zzh.cl.autolibrary.info_api.MyUser;
import com.hsm.zzh.cl.autolibrary.info_api.UserOperation;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BorrowHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView hRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<Book> hbList=new ArrayList<>();
    private BmobUser myUser = BmobUser.getCurrentUser(MyUser.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_history);
        toolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        hRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        hRecyclerView.setLayoutManager(manager);
        hRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
    }

    private void getBorrowHistory() {
        final MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser != null) {
            Log.d("myuserhistory", "getRecord: "+myUser.repr());
        }else {
            Log.d("myuserhistory", "null");
        }
        UserOperation.get_user_borrow_history(new FindListener<Book>() {
            @Override
            public void done(List<Book> list, BmobException e) {
                if (e == null) {
                    hbList.clear();
                    try {
                        for (Book b : list) {
                            if (b.getNow_user() != null && b.getNow_user().getObjectId().equals(myUser.getObjectId())) {
                                hbList.add(b);
                            }
                        }

                        for (Book b : list) {
                            if (b.getNow_user() == null
                                    || !b.getNow_user().getObjectId().equals(myUser.getObjectId())) {
                                hbList.add(b);
                            }
                        }
                    } catch (NullPointerException e1) {
                        e1.printStackTrace();
                    }
                    Log.d("history", Integer.toString(hbList.size()));
                    historyAdapter.notifyDataSetChanged();
                } else {
                    Log.d("history", "fail");
                    Log.d("history", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        getBorrowHistory();

        historyAdapter = new HistoryAdapter(hbList);

        hRecyclerView.setAdapter(historyAdapter);
        super.onResume();
    }
}