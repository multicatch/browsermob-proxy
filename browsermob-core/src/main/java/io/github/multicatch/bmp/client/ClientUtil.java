package io.github.multicatch.bmp.client;

import com.google.common.collect.ImmutableList;
import io.github.multicatch.bmp.BrowserMobProxy;
import io.github.multicatch.bmp.proxy.dns.AdvancedHostResolver;
import io.github.multicatch.bmp.proxy.dns.ChainedHostResolver;
import io.github.multicatch.bmp.proxy.dns.DnsJavaResolver;
import io.github.multicatch.bmp.proxy.dns.NativeCacheManipulatingResolver;
import io.github.multicatch.bmp.proxy.dns.NativeResolver;
import org.openqa.selenium.Proxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * A utility class with convenience methods for clients using BrowserMob Proxy in embedded mode.
 */
public class ClientUtil {
    /**
     * Creates a {@link NativeCacheManipulatingResolver} instance that can be used when
     * calling {@link BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new NativeCacheManipulatingResolver
     */
    public static AdvancedHostResolver createNativeCacheManipulatingResolver() {
        return new NativeCacheManipulatingResolver();
    }

    /**
     * Creates a {@link NativeResolver} instance that <b>does not support cache manipulation</b> that can be used when
     * calling {@link BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new NativeResolver
     */
    public static AdvancedHostResolver createNativeResolver() {
        return new NativeResolver();
    }

    /**
     * Creates a {@link DnsJavaResolver} instance that can be used when
     * calling {@link BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new DnsJavaResolver
     * @deprecated The dnsjava resolver has been deprecated in favor of the standard JVM resolver and will be removed in BMP &gt;2.1.
     */
    public static AdvancedHostResolver createDnsJavaResolver() {
        return new DnsJavaResolver();
    }

    /**
     * Creates a {@link ChainedHostResolver} instance that first attempts to resolve a hostname using a
     * {@link DnsJavaResolver}, then uses {@link NativeCacheManipulatingResolver}.
     * Can be used when calling {@link BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}.
     *
     * @return a new ChainedHostResolver that resolves addresses first using a DnsJavaResolver, then using a NativeCacheManipulatingResolver
     * @deprecated The dnsjava resolver has been deprecated in favor of the standard JVM resolver and will be removed in BMP &gt;2.1.
     */
    public static AdvancedHostResolver createDnsJavaWithNativeFallbackResolver() {
        return new ChainedHostResolver(ImmutableList.of(new DnsJavaResolver(), new NativeCacheManipulatingResolver()));
    }

    /**
     * Creates a Selenium Proxy object from the BrowserMobProxy instance. The BrowserMobProxy must be started. Retrieves the address
     * of the Proxy using {@link #getConnectableAddress()}.
     *
     * @param browserMobProxy started BrowserMobProxy instance to read connection information from
     * @return a Selenium Proxy instance, configured to use the BrowserMobProxy instance as its proxy server
     * @throws java.lang.IllegalStateException if the proxy has not been started.
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(BrowserMobProxy browserMobProxy) {
        return createSeleniumProxy(browserMobProxy, getConnectableAddress());
    }

    /**
     * Creates a Selenium Proxy object from the BrowserMobProxy instance, using the specified connectableAddress as the Selenium Proxy object's
     * proxy address. Determines the port using {@link BrowserMobProxy#getPort()}. The BrowserMobProxy must be started.
     *
     * @param browserMobProxy    started BrowserMobProxy instance to read the port from
     * @param connectableAddress the network address the Selenium Proxy will use to reach this BrowserMobProxy instance
     * @return a Selenium Proxy instance, configured to use the BrowserMobProxy instance as its proxy server
     * @throws java.lang.IllegalStateException if the proxy has not been started.
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(BrowserMobProxy browserMobProxy, InetAddress connectableAddress) {
        return createSeleniumProxy(new InetSocketAddress(connectableAddress, browserMobProxy.getPort()));
    }

    /**
     * Creates a Selenium Proxy object using the specified connectableAddressAndPort as the HTTP proxy server.
     *
     * @param connectableAddressAndPort the network address (or hostname) and port the Selenium Proxy will use to reach its
     *                                  proxy server (the InetSocketAddress may be unresolved).
     * @return a Selenium Proxy instance, configured to use the specified address and port as its proxy server
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(InetSocketAddress connectableAddressAndPort) {
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL);

        String proxyStr = String.format("%s:%d", connectableAddressAndPort.getHostString(), connectableAddressAndPort.getPort());
        proxy.setHttpProxy(proxyStr);
        proxy.setSslProxy(proxyStr);

        return proxy;
    }

    /**
     * Attempts to retrieve a "connectable" address for this device that other devices on the network can use to connect to a local proxy.
     * This is a "reasonable guess" that is suitable in many (but not all) common scenarios.
     * TODO: define the algorithm used to discover a "connectable" local host
     *
     * @return a "reasonable guess" at an address that can be used by other machines on the network to reach this host
     */
    public static InetAddress getConnectableAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Could not resolve localhost", e);
        }
    }
}
