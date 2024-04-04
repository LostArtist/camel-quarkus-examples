package org.acme;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.entity.ContentType;

public class Routes extends RouteBuilder {

    public void process(Exchange exchange) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        String body = exchange.getMessage().getBody(String.class);
        map.put("devices", body);
        String jsonString = new ObjectMapper().writeValueAsString(map);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, ContentType.APPLICATION_JSON);
        exchange.getMessage().setBody(jsonString);

    }

    @Override
    public void configure() throws Exception {
        from("paho:devices")
                .log("Message before marshalling is ${body}")
                // transform this to the proper Java object to avoid dealing with wrong string format
                //                .unmarshal().json(Pojo.class)
                //                .setBody(constant("{'test':'again'}"))
                .process(this::process)
                .to("elasticsearch-rest-client:docker-cluster?hostAddressesList={{elasticsearch.host}}&operation=INDEX_OR_UPDATE&indexName=items");
    }

}
