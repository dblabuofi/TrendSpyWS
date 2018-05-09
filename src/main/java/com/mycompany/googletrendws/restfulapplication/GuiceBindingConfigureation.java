/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.restfulapplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.mycompany.googletrendws.SegTree.SegTree;
import com.mycompany.googletrendws.googletrends.GoogleTrends;
import com.mycompany.googletrendws.googletrends.services.GoogleTrendMockService;
import com.mycompany.googletrendws.helper.MySQLConn;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 *
 * @author jupiter
 */
public class GuiceBindingConfigureation extends AbstractModule {

    @Override
    public void configure() {
        GoogleTrends googleTrends = new GoogleTrends();
        binder().bind(GoogleTrends.class).toInstance(googleTrends);

        Gson gson = new GsonBuilder().create();
        binder().bind(Gson.class).toInstance(gson);

        MySQLConn mySQLCon = new MySQLConn();
        binder().bind(MySQLConn.class).toInstance(mySQLCon);
        GoogleTrendMockService googleTrendMockService = new GoogleTrendMockService();
        binder().bind(GoogleTrendMockService.class).toInstance(googleTrendMockService);

        Random random = new Random();
        binder().bind(Random.class).toInstance(random);
        
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        binder().bind(DateTimeFormatter.class).toInstance(formatter2);
        
        SegTree segTree = new SegTree(LocalDate.parse("2010-01-01"), LocalDate.parse("2017-12-31"));
        binder().bind(SegTree.class).toInstance(segTree);

    }
}
