package io.github.multicatch.bmp.proxy.test.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.multicatch.bmp.proxy.BrowserMobProxyServerLegacyAdapter;
import io.github.multicatch.bmp.proxy.ProxyManager;
import io.github.multicatch.bmp.proxy.guice.ConfigModule;
import org.junit.After;
import org.junit.Before;

public abstract class ProxyManagerTest {
    protected ProxyManager proxyManager;

    public abstract String[] getArgs();

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ConfigModule(getArgs()));
        proxyManager = injector.getInstance(ProxyManager.class);
    }

    @After
    public void tearDown() throws Exception {
        for(BrowserMobProxyServerLegacyAdapter p : proxyManager.get()){
            try{
                proxyManager.delete(p.getPort());
            }catch(Exception e){ }
        }
    }

}
