/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.entity.ContentType;

public class Routes extends RouteBuilder {

    public void process(Exchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();

        //get message from pojo class
        String body = exchange.getMessage().getBody(String.class);
        map.put("devices", body);

        //convert map to json string
        String jsonString = new ObjectMapper().writeValueAsString(map);

        //define content type
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, ContentType.APPLICATION_JSON);

        //set json string message for the exchange
        exchange.getMessage().setBody(jsonString);

    }

    @Override
    public void configure() {
        from("paho:devices")
                .log("Message before marshalling is ${body}")
                .process(this::process)
                .to("elasticsearch-rest-client:docker-cluster?hostAddressesList={{elasticsearch.host}}&operation=INDEX_OR_UPDATE&indexName=devices");
    }

}
