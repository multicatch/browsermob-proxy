package io.github.multicatch.bmp.mitm


import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpVersion
import org.junit.Test

import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLSession

import static org.junit.Assert.assertNotNull
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ImpersonatingMitmManagerTest {
    SSLSession mockSession = mock(SSLSession)

    @Test
    void testCreateDefaultServerEngine() {
        io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager mitmManager = io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager.builder().build()

        SSLEngine serverSslEngine = mitmManager.serverSslEngine("hostname", 80)
        assertNotNull(serverSslEngine)
    }

    @Test
    void testCreateDefaultClientEngine() {
        io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager mitmManager = io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager.builder().build()

        when(mockSession.getPeerHost()).thenReturn("hostname")

        def request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, "https://test.connection")
        SSLEngine clientSslEngine = mitmManager.clientSslEngineFor(request, mockSession)
        assertNotNull(clientSslEngine)
    }

    @Test
    void testCreateCAAndServerCertificatesOfDifferentTypes() {
        io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager mitmManager = io.github.multicatch.bmp.mitm.manager.ImpersonatingMitmManager.builder()
                .rootCertificateSource(RootCertificateGenerator.builder().keyGenerator(new io.github.multicatch.bmp.mitm.keys.RSAKeyGenerator()).build())
                .serverKeyGenerator(new io.github.multicatch.bmp.mitm.keys.ECKeyGenerator())
                .build()

        when(mockSession.getPeerHost()).thenReturn("hostname")

        def request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, "https://test.connection")
        SSLEngine clientSslEngine = mitmManager.clientSslEngineFor(request, mockSession)
        assertNotNull(clientSslEngine)
    }
}
