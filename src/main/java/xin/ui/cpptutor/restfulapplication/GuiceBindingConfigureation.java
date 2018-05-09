/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.restfulapplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import xin.ui.cpptutor.dao.MySQLCon;
import xin.ui.cpptutor.dao.SQLData;
import xin.ui.cpptutor.matching.MatchingService;
import xin.ui.cpptutor.parser.EPDGParser;

/**
 *
 * @author jupiter
 */
public class GuiceBindingConfigureation extends AbstractModule {

    @Override
    public void configure() {
        Gson gson = new GsonBuilder().setDateFormat("MM-dd-yyyy").create();
        binder().bind(Gson.class).toInstance(gson);
        MatchingService service = new MatchingService();
        binder().bind(MatchingService.class).toInstance(service);
        EPDGParser parser = new EPDGParser();
        binder().bind(EPDGParser.class).toInstance(parser);

        MySQLCon mySQLCon = new MySQLCon();
        binder().bind(MySQLCon.class).toInstance(mySQLCon);
    }
}
