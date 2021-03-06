package com.hsm.zzh.cl.autolibrary.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.activity.BorrowHistoryActivity;
import com.hsm.zzh.cl.autolibrary.activity.ChangeInfoActivity;
import com.hsm.zzh.cl.autolibrary.activity.LoginActivity;
import com.hsm.zzh.cl.autolibrary.activity.RegisterActivity;
import com.hsm.zzh.cl.autolibrary.info_api.MyUser;
import com.hsm.zzh.cl.autolibrary.info_api.UserOperation;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;

public class UserFragment extends Fragment implements View.OnClickListener{

    private ViewGroup group_HasLogin;
    private ViewGroup group_NoLogin;

    private TextView view_userAccount;
    private TextView view_login;
    private TextView view_register;
    private TextView view_showBalance;
    private View view_record;
    private TextView view_changeInfo;
    private CardView view_about;
    private CardView view_service;
    private Button view_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        group_HasLogin = (ViewGroup) view.findViewById(R.id.has_login_view);
        group_NoLogin = (ViewGroup) view.findViewById(R.id.no_login_view);
        view_userAccount = (TextView) view.findViewById(R.id.user_name);
        view_login = (TextView) view.findViewById(R.id.login);

        view_register = (TextView) view.findViewById(R.id.register);
        view_record = (View) view.findViewById(R.id.record);
        view_changeInfo = (TextView) view.findViewById(R.id.change_info);
        view_about = (CardView) view.findViewById(R.id.card_about);
        view_service = (CardView) view.findViewById(R.id.card_service);
        view_logout = (Button) view.findViewById(R.id.logout);
        view_showBalance = (TextView) view.findViewById(R.id.show_balance);

        init_view();
        return view;
    }

    private void init_view(){

        view_userAccount.setOnClickListener(this);
        view_login.setOnClickListener(this);
        view_register.setOnClickListener(this);
        view_record.setOnClickListener(this);
        view_changeInfo.setOnClickListener(this);
        view_about.setOnClickListener(this);
        view_service.setOnClickListener(this);
        view_logout.setOnClickListener(this);

        check_login();
    }

    private MyUser curUser = null;

    private void check_login(){
        if(BmobUser.isLogin()){
            group_HasLogin.setVisibility(View.VISIBLE);
            group_NoLogin.setVisibility(View.INVISIBLE);

            curUser = BmobUser.getCurrentUser(MyUser.class);
            view_userAccount.setText(curUser.getUsername());

            String temp = "余额："+curUser.getAccount_balance();
            view_showBalance.setText(temp);
        }else{
            curUser = null;
            group_HasLogin.setVisibility(View.INVISIBLE);
            group_NoLogin.setVisibility(View.VISIBLE);
        }
    }

    private final int LOGIN_CODE = 1;
    private final int REGISTER_CODE = 2;
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login:{
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent,  LOGIN_CODE);
                break;
            }
            case R.id.register:{
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER_CODE);
                break;
            }
            case R.id.logout:{
                UserOperation.login_out();
                check_login();
                break;
            }
            case R.id.record:{
                if(curUser == null)
                    return ;
                Intent intent = new Intent(getContext(), BorrowHistoryActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.change_info:{
                if(curUser == null)
                    return ;
                Intent intent = new Intent(getContext(), ChangeInfoActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return ;
        }
        switch(requestCode){
            case LOGIN_CODE:{
                check_login();
                break;
            }case REGISTER_CODE:{
                check_login();
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        check_login();
    }
}
