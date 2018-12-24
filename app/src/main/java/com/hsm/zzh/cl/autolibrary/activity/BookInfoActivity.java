package com.hsm.zzh.cl.autolibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.info_api.Book;
import com.hsm.zzh.cl.autolibrary.info_api.BookOperation;
import com.hsm.zzh.cl.autolibrary.info_api.Callback;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class BookInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button collectBtn;

    private Button borrowBtn;

    private Toolbar toolBar;

    private ImageView bookImage;

    private TextView titleText;

    private TextView authorText;

    private TextView type;

    private TextView descText;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("图书信息");
        }

        book = (Book) getIntent().getSerializableExtra("selected_book");
        bookImage = (ImageView) findViewById(R.id.image_info);
        titleText = (TextView) findViewById(R.id.title_info);
        authorText = (TextView) findViewById(R.id.author_info);
        type = (TextView) findViewById(R.id.type_info);
        descText = (TextView) findViewById(R.id.info_desc);
        collectBtn = (Button) findViewById(R.id.info_collect);
        borrowBtn = (Button) findViewById(R.id.info_borrow);

        if (book != null) {
            Glide.with(this).load(book.getPic_url()).into(bookImage);
            titleText.setText(book.getTitle());
            authorText.setText(book.getAuthor());
            type.setText(book.getType());
            descText.setText(book.getShortdesc());
            collectBtn.setOnClickListener(this);
            borrowBtn.setOnClickListener(this);

        }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_collect:
                // TODO: 18-12-18
                break;
            case R.id.info_borrow:
                // TODO: 18-12-18
//                if(!BmobUser.isLogin()){
//                    return ;
//                }

                Intent intent = new Intent(this, BookLocationActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
