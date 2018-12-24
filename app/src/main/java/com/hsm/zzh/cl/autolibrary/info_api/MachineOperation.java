package com.hsm.zzh.cl.autolibrary.info_api;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MachineOperation {

    /**
     * 通过一个 objectid 获取机器信息
     *
     * @param id            一个机器的 objectid
     * @param queryListener
     */
    public static void get_one_machine_buy_id(String id, QueryListener<Machine> queryListener) {

        BmobQuery<Machine> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getObject(id, queryListener);

    }  // 通过id获取机器


    /**
     * 获得所有机器
     *
     * @param findListener
     */
    public static void get_machines(FindListener<Machine> findListener) {

        BmobQuery<Machine> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findObjects(findListener);

    }   //获取所有机器


    /**
     * @param bmobGeoPoint 一个位置
     * @param dis          一个距离
     * @param findListener
     */
    public static void get_machines_by_location(BmobGeoPoint bmobGeoPoint, Double dis, FindListener<Machine> findListener) {

        BmobQuery<Machine> query = new BmobQuery<>();
        query.addWhereNear("location", bmobGeoPoint);
//        query.addWhereEqualTo("location",bmobGeoPoint);
        query.addWhereWithinKilometers("location", bmobGeoPoint, dis);
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findObjects(findListener);

    } //获取一个位置附近的机器


    /**
     * 一本书当前的机器只需要调用方法
     *
     * @param book
     */
    public static void get_machines_by_book(Book book, FindListener<Machine> findListener) {

        BmobQuery<Machine> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", book.getNow_machine().getObjectId());
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(findListener);
    }


    /**
     * 获取一本书曾经放过的机器
     *
     * @param book
     * @param findListener
     */
    public static void get_history_machine(Book book, FindListener<Machine> findListener) {

        List<String> list = new ArrayList<String>();
        for (BmobPointer b : book.getHistory_machine().getObjects()) {
            list.add(b.getObjectId());
        }


        BmobQuery<Machine> query = new BmobQuery<Machine>();
        query.addWhereContainedIn("objectId", list);
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findObjects(findListener);

    }

}
