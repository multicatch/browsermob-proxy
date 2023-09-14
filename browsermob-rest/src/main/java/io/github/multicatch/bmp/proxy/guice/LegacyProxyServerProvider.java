package io.github.multicatch.bmp.proxy.guice;

import com.google.inject.Provider;
import io.github.multicatch.bmp.proxy.BrowserMobProxyServerLegacyAdapter;

public class LegacyProxyServerProvider implements Provider<BrowserMobProxyServerLegacyAdapter> {
    @Override
    public BrowserMobProxyServerLegacyAdapter get() {
        return new BrowserMobProxyServerLegacyAdapter();
    }
}
