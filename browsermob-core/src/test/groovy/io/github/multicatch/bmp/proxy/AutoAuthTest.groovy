package io.github.multicatch.bmp.proxy


import org.apache.http.client.methods.HttpGet
import org.junit.After
import org.junit.Test
import org.mockserver.matchers.Times
import org.mockserver.model.NottableString

import static org.junit.Assert.assertEquals
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class AutoAuthTest extends io.github.multicatch.bmp.proxy.test.util.MockServerTest {
    io.github.multicatch.bmp.BrowserMobProxy proxy

    @After
    void tearDown() {
        if (proxy?.started) {
            proxy.abort()
        }
    }

    @Test
    void testBasicAuthAddedToHttpRequest() {
        // the base64-encoded rendering of "testUsername:testPassword" is dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA==
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/basicAuthHttp")
                .withHeader("Authorization", "Basic dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA=="),
                Times.exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withBody("success"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer();
        proxy.autoAuthorization("localhost", "testUsername", "testPassword", io.github.multicatch.bmp.proxy.auth.AuthType.BASIC)
        proxy.setTrustAllServers(true)
        proxy.start()

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxy.port).withCloseable {
            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(it.execute(new HttpGet("http://localhost:${mockServerPort}/basicAuthHttp")).getEntity().getContent());
            assertEquals("Did not receive expected response from mock server", "success", responseBody);
        };
    }

    @Test
    void testBasicAuthAddedToHttpsRequest() {
        // the base64-encoded rendering of "testUsername:testPassword" is dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA==
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/basicAuthHttp")
                .withHeader("Authorization", "Basic dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA=="),
                Times.exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withBody("success"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer();
        proxy.autoAuthorization("localhost", "testUsername", "testPassword", io.github.multicatch.bmp.proxy.auth.AuthType.BASIC)
        proxy.setTrustAllServers(true)
        proxy.start()

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxy.port).withCloseable {
            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(it.execute(new HttpGet("https://localhost:${mockServerPort}/basicAuthHttp")).getEntity().getContent());
            assertEquals("Did not receive expected response from mock server", "success", responseBody);
        };
    }

    @Test
    void testCanStopBasicAuth() {
        // the base64-encoded rendering of "testUsername:testPassword" is dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA==
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/basicAuthHttp")
                // require that the Auth header NOT be present
                .withHeader(NottableString.not("Authorization"), NottableString.not("Basic dGVzdFVzZXJuYW1lOnRlc3RQYXNzd29yZA==")),
                Times.exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withBody("success"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer();
        proxy.autoAuthorization("localhost", "testUsername", "testPassword", io.github.multicatch.bmp.proxy.auth.AuthType.BASIC)
        proxy.setTrustAllServers(true)
        proxy.start()

        proxy.stopAutoAuthorization("localhost")

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxy.port).withCloseable {
            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(it.execute(new HttpGet("http://localhost:${mockServerPort}/basicAuthHttp")).getEntity().getContent());
            assertEquals("Did not receive expected response from mock server", "success", responseBody);
        };
    }
}
