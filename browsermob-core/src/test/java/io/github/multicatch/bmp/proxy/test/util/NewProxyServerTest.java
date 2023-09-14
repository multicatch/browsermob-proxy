package io.github.multicatch.bmp.proxy.test.util;

import io.github.multicatch.bmp.BrowserMobProxy;
import io.github.multicatch.bmp.BrowserMobProxyServer;
import org.junit.After;
import org.junit.Before;

/**
 * A base class that spins up and shuts down a BrowserMobProxy instance using the new interface. IT also provides mock server support via
 * {@link MockServerTest}.
 */
public class NewProxyServerTest extends MockServerTest {
    protected BrowserMobProxy proxy;

    @Before
    public void setUpProxyServer() {
        proxy = new BrowserMobProxyServer();
        proxy.start();
    }

    @After
    public void shutDownProxyServer() {
        if (proxy != null) {
            proxy.abort();
        }
    }

}
