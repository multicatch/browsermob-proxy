package io.github.multicatch.bmp.proxy;

import io.github.multicatch.bmp.exception.ProxyExistsException;
import io.github.multicatch.bmp.exception.ProxyPortsExhaustedException;
import io.github.multicatch.bmp.proxy.test.util.ProxyManagerTest;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProxyPortAssignmentTest extends ProxyManagerTest {
        
    @Override
    public String[] getArgs() {        
        return new String[]{"--proxyPortRange", "9091-9093"};
    }

    @Test
    public void testAutoAssignment() throws Exception {
        int[] ports = {9091, 9092, 9093};
        BrowserMobProxyServerLegacyAdapter p;
        for(int port : ports){
            p = proxyManager.create(new HashMap<String, String>());
            assertEquals(port, p.getPort());
        }
        try{
            proxyManager.create(new HashMap<String, String>());        
            fail();
        }catch(ProxyPortsExhaustedException e){
            proxyManager.delete(9093);
            p = proxyManager.create(new HashMap<String, String>());
            assertEquals(9093, p.getPort());
            
            proxyManager.delete(9091);
            p = proxyManager.create(new HashMap<String, String>());
            assertEquals(9091, p.getPort());
                    
            for(int port : ports){
                proxyManager.delete(port);
            }
        }
    }
    
    @Test
    public void testManualAssignment() throws Exception {
        BrowserMobProxyServerLegacyAdapter p = proxyManager.create(new HashMap<String, String>(), 9094);
        assertEquals(9094, p.getPort());
        try{            
            proxyManager.create(new HashMap<String, String>(), 9094);            
            fail();
        }catch(ProxyExistsException e){
            assertEquals(9094, e.getPort());
            proxyManager.delete(9094);
        }        
    }
}
