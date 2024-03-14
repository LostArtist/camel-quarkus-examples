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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.json.JsonObject;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

@ApplicationScoped
@Named
public class IndexProcessing implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("new", "one");

        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9300, "http")).build();

        Request request = new Request("POST", "/");
        Response response = restClient.performRequest(request);

        request.addParameter("lets", "try");
        restClient.performRequestAsync(request, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {

                try {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonObject jsonObject = convertHttpEntityToJsonObject(responseBody);
                    exchange.getMessage().setBody(extractID(jsonObject));
                } catch (IOException e) {
                    exchange.setException(e);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        /*        String indexId;
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("try", "me");*/

        //        String indexName = "nam";
        //        String responseBody = EntityUtils.toString(response.getEntity());
        //        JsonObject doc = convertHttpEntityToJsonObject(responseBody);
        //        exchange.getMessage()
        //                .setBody(extractID(doc));

    }

    private JsonObject convertHttpEntityToJsonObject(String httpResponse) throws IOException {
        // Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert JSON content to Map<String, Object>
        Map<String, Object> map = objectMapper.readValue(httpResponse, new TypeReference<>() {
        });
        // convert to JsonObject
        return new JsonObject(map);
    }

    String extractID(JsonObject doc) {
        return doc.getString("_id");
    }

    /*new IndexRequest.Builder<HashMap<String, String>>()
            .index("heyyou")
                        .document(map));*/
    //
}
