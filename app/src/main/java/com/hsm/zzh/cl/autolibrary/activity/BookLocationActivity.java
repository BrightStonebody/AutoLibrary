package com.hsm.zzh.cl.autolibrary.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.fragment.MapFragment;
import com.hsm.zzh.cl.autolibrary.info_api.Book;
import com.hsm.zzh.cl.autolibrary.info_api.Machine;
import com.hsm.zzh.cl.autolibrary.info_api.MachineOperation;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BookLocationActivity extends AppCompatActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("图书定位");
        }

        Intent intent = getIntent();
        if(!intent.hasExtra("book")){
            Log.e("错误", "onCreate: BookLocationActivity getIntent()时没有book参数");
            return ;
        }
        final Book book = (Book) intent.getSerializableExtra("book");
        MachineOperation.get_machines_by_book(book, new FindListener<Machine>() {
            @Override
            public void done(final List<Machine> list, BmobException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapFragment.addFindBookLocation(list);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                break;
            }
        }
        return true;
    }
}
