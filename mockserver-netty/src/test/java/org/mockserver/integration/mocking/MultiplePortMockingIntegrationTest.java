package org.mockserver.integration.mocking;

import com.google.common.base.Joiner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.integration.server.AbstractBasicMockingIntegrationTest;
import org.mockserver.model.MediaType;
import org.mockserver.socket.PortFactory;

import java.util.List;
import java.util.Random;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockserver.character.Character.NEW_LINE;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;
import static org.mockserver.stop.Stop.stopQuietly;

/**
 * @author jamesdbloom
 */
public class MultiplePortMockingIntegrationTest extends AbstractBasicMockingIntegrationTest {

    private static Integer[] severHttpPort;
    private final Random random = new Random();

    @BeforeClass
    public static void startServer() {
        mockServerClient = startClientAndServer(0, PortFactory.findFreePort(), 0, PortFactory.findFreePort());
        List<Integer> boundPorts = ((ClientAndServer) mockServerClient).getLocalPorts();
        severHttpPort = boundPorts.toArray(new Integer[boundPorts.size()]);
    }

    @AfterClass
    public static void stopServer() {
        stopQuietly(mockServerClient);
    }

    @Override
    public int getServerPort() {
        return severHttpPort[random.nextInt(severHttpPort.length)];
    }

    @Test
    public void shouldReturnStatus() {
        // then
        // - in http
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + Joiner.on(", ").join(severHttpPort) + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withPath(calculatePath("mockserver/status"))
                    .withMethod("PUT"),
                headersToIgnore)
        );
        // - in https
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + Joiner.on(", ").join(severHttpPort) + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withSecure(true)
                    .withPath(calculatePath("mockserver/status"))
                    .withMethod("PUT"),
                headersToIgnore)
        );
    }

    @Test
    public void shouldBindToNewSocket() {
        // given
        int firstNewPort = PortFactory.findFreePort();
        int secondNewPort = PortFactory.findFreePort();

        // then
        // - in http
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + firstNewPort + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withPath(calculatePath("mockserver/bind"))
                    .withMethod("PUT")
                    .withBody("{" + NEW_LINE +
                        "  \"ports\" : [ " + firstNewPort + " ]" + NEW_LINE +
                        "}"),
                headersToIgnore)
        );
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + Joiner.on(", ").join(severHttpPort) + ", " + firstNewPort + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withPath(calculatePath("mockserver/status"))
                    .withMethod("PUT"),
                headersToIgnore)
        );
        // - in https
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + secondNewPort + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withSecure(true)
                    .withPath(calculatePath("mockserver/bind"))
                    .withMethod("PUT")
                    .withBody("{" + NEW_LINE +
                        "  \"ports\" : [ " + secondNewPort + " ]" + NEW_LINE +
                        "}"),
                headersToIgnore)
        );
        assertEquals(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .withBody("{" + NEW_LINE +
                    "  \"ports\" : [ " + Joiner.on(", ").join(severHttpPort) + ", " + firstNewPort + ", " + secondNewPort + " ]" + NEW_LINE +
                    "}", MediaType.JSON_UTF_8),
            makeRequest(
                request()
                    .withSecure(true)
                    .withPath(calculatePath("mockserver/status"))
                    .withMethod("PUT")
                    .withBody("{" + NEW_LINE +
                        "  \"ports\" : [ " + firstNewPort + " ]" + NEW_LINE +
                        "}"),
                headersToIgnore)
        );
    }
}
