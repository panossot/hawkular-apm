/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.apm.tests.client.wildfly.camel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.hawkular.apm.api.model.trace.Consumer;
import org.hawkular.apm.api.model.trace.Trace;
import org.hawkular.apm.tests.common.ClientTestBase;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * These tests invoke a Camel based REST service to cause business transaction information
 * to be reported to the BusinessTransactionService.
 *
 * @author gbrown
 */
public class ClientCamelCXFTest extends ClientTestBase {

    private static final String REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
            +
            "<soap:Body>\r\n" +
            "    <ns1:reportIncident xmlns:ns1=\"http://incident.cxf.example.camel.apache.org/\">\r\n" +
            "        <arg0>\r\n" +
            "            <details>blah blah</details>\r\n" +
            "            <email>davsclaus@apache.org</email>\r\n" +
            "            <familyName>Smith</familyName>\r\n" +
            "            <givenName>Bob</givenName>\r\n" +
            "            <incidentDate>2011-11-25</incidentDate>\r\n" +
            "            <incidentId>123</incidentId>\r\n" +
            "            <phone>123-456-7890</phone>\r\n" +
            "            <summary>blah blah summary</summary>\r\n" +
            "        </arg0>\r\n" +
            "    </ns1:reportIncident>\r\n" +
            "</soap:Body>\r\n" +
            "</soap:Envelope>";

    @Override
    public int getPort() {
        return 8180;
    }

    @Test
    public void testInvokeCamelCXFService() {

        // Delay to avoid picking up previously reported txns
        try {
            synchronized (this) {
                wait(500);
            }
        } catch (Exception e) {
            fail("Failed to wait");
        }

        try {
            URL url = new URL(System.getProperty("test.uri")
                    + "/camel-example-cxftomcat/webservices/incident");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                    "application/soap+xml");

            java.io.OutputStream os = connection.getOutputStream();

            os.write(REQUEST.getBytes());

            os.flush();
            os.close();

            java.io.InputStream is = connection.getInputStream();

            byte[] b = new byte[is.available()];

            is.read(b);

            is.close();

            assertEquals(200, connection.getResponseCode());

            String resp = new String(b);

            System.out.println(">>> RESP = " + resp);

            assertTrue("Response should contain '<code>OK;123</code>'",
                    resp.contains("<code>OK;123</code>"));
        } catch (Exception e) {
            fail("Failed to get cxf response: " + e);
        }

        // Need to wait for trace fragment to be reported to server
        try {
            synchronized (this) {
                wait(3000);
            }
        } catch (Exception e) {
            fail("Failed to wait");
        }

        // Check if trace fragments have been reported
        List<Trace> btxns = getApmMockServer().getTraces();

        for (Trace trace : btxns) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                System.out.println("BTXN=" + mapper.writeValueAsString(trace));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        assertEquals(1, btxns.size());

        // Check top level node is a Consumer associated with the servlet
        assertEquals(Consumer.class, btxns.get(0).getNodes().get(0).getClass());

        // Check that there is request and response message content
        Consumer consumer = (Consumer) btxns.get(0).getNodes().get(0);

        assertNotNull(consumer.getIn());
        assertNotNull(consumer.getOut());
        assertTrue(consumer.getIn().getContent().containsKey("all"));
        assertTrue(consumer.getOut().getContent().containsKey("all"));
        assertNotNull(consumer.getIn().getContent().get("all").getValue());
        assertNotNull(consumer.getOut().getContent().get("all").getValue());
        assertTrue(consumer.getIn().getContent().get("all").getValue().length() > 0);
        assertTrue(consumer.getOut().getContent().get("all").getValue().length() > 0);
    }

}
