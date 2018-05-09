/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.restfulapplication;

import com.google.inject.Guice;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

/**
 *
 * @author jupiter
 */
@ApplicationPath("resources")
public class RESTfulApplication extends ResourceConfig  {
        @Inject
    public RESTfulApplication(ServiceLocator locator) {
        packages("com.mycompany.mindreadertran.resources");
         GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        
        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(Guice.createInjector(new GuiceBindingConfigureation() ));
    }
}
