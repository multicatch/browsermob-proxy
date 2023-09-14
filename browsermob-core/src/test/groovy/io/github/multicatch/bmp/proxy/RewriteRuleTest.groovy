package io.github.multicatch.bmp.proxy


import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.junit.After
import org.junit.Test
import org.mockserver.matchers.Times

import static org.junit.Assert.assertEquals
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class RewriteRuleTest extends io.github.multicatch.bmp.proxy.test.util.MockServerTest {
    private io.github.multicatch.bmp.BrowserMobProxy proxy

    @After
    void tearDown() {
        if (proxy?.started) {
            proxy.abort()
        }
    }

    @Test
    void testRewriteHttpUrl() {
        mockServer.when(request()
                .withMethod("GET")
                .withPath("/")
                .withQueryStringParameter("originalDomain", "yahoo")
                .withQueryStringParameter("param1", "value1"),
                Times.once())
                .respond(response()
                .withStatusCode(200)
                .withBody("success"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer();
        proxy.rewriteUrl('http://www\\.(yahoo|bing)\\.com/\\?(\\w+)=(\\w+)', 'http://localhost:' + mockServerPort + '/?originalDomain=$1&$2=$3');
        proxy.setTrustAllServers(true)
        proxy.start()

        String requestUrl = "http://www.yahoo.com?param1=value1"

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxy.port).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet(requestUrl))
            assertEquals("Did not receive HTTP 200 from mock server", 200, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent());
            assertEquals("Did not receive expected response from mock server", "success", responseBody);
        };
    }

    @Test
    void testRewriteHttpsUrl() {
        // HTTPS URLs cannot currently be rewritten to another domain, so verify query parameters can be rewritten

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/")
                .withQueryStringParameter("firstParam", "param1")
                .withQueryStringParameter("firstValue", "value1"),
                Times.once())
                .respond(response()
                .withStatusCode(200)
                .withBody("success"))

        proxy = new io.github.multicatch.bmp.BrowserMobProxyServer();
        proxy.rewriteUrl('https://localhost:' + mockServerPort + '/\\?(\\w+)=(\\w+)', 'https://localhost:' + mockServerPort + '/?firstParam=$1&firstValue=$2');
        proxy.setTrustAllServers(true)
        proxy.start()

        String requestUrl = "https://localhost:$mockServerPort?param1=value1"

        io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.getNewHttpClient(proxy.port).withCloseable {
            CloseableHttpResponse response = it.execute(new HttpGet(requestUrl))
            assertEquals("Did not receive HTTP 200 from mock server", 200, response.getStatusLine().getStatusCode())

            String responseBody = io.github.multicatch.bmp.proxy.test.util.NewProxyServerTestUtil.toStringAndClose(response.getEntity().getContent());
            assertEquals("Did not receive expected response from mock server", "success", responseBody);
        };
    }
}
