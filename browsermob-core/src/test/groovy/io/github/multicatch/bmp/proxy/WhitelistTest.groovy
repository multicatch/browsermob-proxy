package io.github.multicatch.bmp.proxy


import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpVersion
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.junit.After
import org.junit.Test
import org.mockserver.matchers.Times

import static org.hamcrest.Matchers.isEmptyOrNullString
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class WhitelistTest extends io.github.multicatch.bmp.proxy.test.util.MockServerTest {
    io.github.multicatch.bmp.BrowserMobProxy proxy

    @After
    void tearDown() {
        if (proxy?.started) {
            proxy.abort()
        }
    }

    @Test
    void testWhitelistCannotShortCircuitCONNECT() {
        HttpRequest request = mock(HttpRequest.class)
        when(request.getMethod()).thenReturn(HttpMethod.CONNECT)
        when(request.getUri()).thenReturn('somedomain.com:443')
        when(request.getProtocolVersion()).thenReturn(HttpVersion.HTTP_1_1)

        // create a whitelist filter that whitelists no requests (i.e., all requests should return the specified HTTP 500 status code)
        io.github.multicatch.bmp.filters.WhitelistFilter filter = new io.github.multicatch.bmp.filters.WhitelistFilter(request, null, true, 500, [])
        HttpResponse response = filter.clientToProxyRequest(request)

        assertNull("Whitelist short-circuited HTTP CONNECT. Expected all HTTP CONNECTs to be whitelisted.", response)
    }

    @Test
    void testNonWhitelistedHttpRequestReturnsWhitelistStatusCode() {
        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["http://localhost/.*"], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet("http://www.someother.domain/someresource"))
            assertEquals("Did not receive whitelist status code in response", 500, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent())
            assertThat("Expected whitelist response to contain 0-length body", responseBody, isEmptyOrNullString())
        }
    }

    @Test
    void testNonWhitelistedHttpsRequestReturnsWhitelistStatusCode() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/nonwhitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("should never be returned"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.setTrustAllServers(true)
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["https://some-other-domain/.*"], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet("https://localhost:${mockServerPort}/nonwhitelistedresource"))
            assertEquals("Did not receive whitelist status code in response", 500, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent())
            assertThat("Expected whitelist response to contain 0-length body", responseBody, isEmptyOrNullString())
        }
    }

    @Test
    void testWhitelistedHttpRequestNotShortCircuited() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/whitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("whitelisted"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["http://localhost:${mockServerPort}/.*".toString()], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet("http://localhost:${mockServerPort}/whitelistedresource"))
            assertEquals("Did not receive expected response from mock server for whitelisted url", 200, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent())
            assertEquals("Did not receive expected response body from mock server for whitelisted url", "whitelisted", responseBody)
        }
    }

    @Test
    void testWhitelistedHttpsRequestNotShortCircuited() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/whitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("whitelisted"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.setTrustAllServers(true)
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["https://localhost:${mockServerPort}/.*".toString()], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet("https://localhost:${mockServerPort}/whitelistedresource"))
            assertEquals("Did not receive expected response from mock server for whitelisted url", 200, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent())
            assertEquals("Did not receive expected response body from mock server for whitelisted url", "whitelisted", responseBody)
        }
    }

    @Test
    void testCanWhitelistSpecificHttpResource() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/whitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("whitelisted"))

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/nonwhitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("should never be returned"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["http://localhost:${mockServerPort}/whitelistedresource".toString()], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse nonWhitelistedResponse = it.execute(new HttpGet("http://localhost:${mockServerPort}/nonwhitelistedresource"))
            assertEquals("Did not receive whitelist status code in response", 500, nonWhitelistedResponse.getStatusLine().getStatusCode())

            String nonWhitelistedResponseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(nonWhitelistedResponse.getEntity().getContent())
            assertThat("Expected whitelist response to contain 0-length body", nonWhitelistedResponseBody, isEmptyOrNullString())

            CloseableHttpResponse whitelistedResponse = it.execute(new HttpGet("http://localhost:${mockServerPort}/whitelistedresource"))
            assertEquals("Did not receive expected response from mock server for whitelisted url", 200, whitelistedResponse.getStatusLine().getStatusCode())

            String whitelistedResponseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(whitelistedResponse.getEntity().getContent())
            assertEquals("Did not receive expected response body from mock server for whitelisted url", "whitelisted", whitelistedResponseBody)
        }
    }

    @Test
    void testCanWhitelistSpecificHttpsResource() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/whitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("whitelisted"))

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/nonwhitelistedresource"),
                Times.unlimited())
                .respond(response()
                .withStatusCode(200)
                .withBody("should never be returned"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer()
        proxy.setTrustAllServers(true)
        proxy.start()
        int proxyPort = proxy.getPort()

        proxy.whitelistRequests(["https://localhost:${mockServerPort}/whitelistedresource".toString()], 500)

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxyPort).withCloseable {
            CloseableHttpResponse nonWhitelistedResponse = it.execute(new HttpGet("https://localhost:${mockServerPort}/nonwhitelistedresource"))
            assertEquals("Did not receive whitelist status code in response", 500, nonWhitelistedResponse.getStatusLine().getStatusCode())

            String nonWhitelistedResponseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(nonWhitelistedResponse.getEntity().getContent())
            assertThat("Expected whitelist response to contain 0-length body", nonWhitelistedResponseBody, isEmptyOrNullString())

            CloseableHttpResponse whitelistedResponse = it.execute(new HttpGet("https://localhost:${mockServerPort}/whitelistedresource"))
            assertEquals("Did not receive expected response from mock server for whitelisted url", 200, whitelistedResponse.getStatusLine().getStatusCode())

            String whitelistedResponseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(whitelistedResponse.getEntity().getContent())
            assertEquals("Did not receive expected response body from mock server for whitelisted url", "whitelisted", whitelistedResponseBody)
        }
    }
}
