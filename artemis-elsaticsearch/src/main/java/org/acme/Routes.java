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
import java.util.Map;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.json.JsonObject;

import static org.apache.camel.model.rest.RestBindingMode.json;

//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.xcontent.XContentType;

public class Routes extends RouteBuilder {

    public void process(Exchange exchange) throws IOException {
        //        RestClient httpClient = RestClient.builder(
        //                new HttpHost("localhost", 9200)).build();
        //        ElasticsearchTransport transport = new RestClientTransport(
        //                httpClient,
        //                new JacksonJsonpMapper());
        //        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        //        ObjectMapper mapper = new ObjectMapper();
        //        Pojo text = mapper.reader().forType(Pojo.class).readValue(body);
        //JsonObject json = new JsonObject(body.toString());
        //        String jsonString = mapper.writeValueAsString(body);

        Pojo body = exchange.getMessage().getBody(Pojo.class);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);
        BulkRequest.Builder br = new BulkRequest.Builder();
        br.operations(op -> op.index(idx -> idx.index("devices").id("1").document(json)));
        exchange.getMessage().setBody(br.build());

        //        IndexResponse response = esClient.index(i -> i.index("devices").id("1").document(body));

        //        IndexRequest indexRequest = new IndexRequest("sampleIndex");
        //        indexRequest.id("003");
        //        indexRequest.source(new ObjectMapper().writeValueAsString(emp), XContentType.JSON);
        //        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        //        System.out.println("response id: " + indexResponse.getId());
        //        System.out.println("response name: " + indexResponse.getResult().name());

        //        String response = "{'devices':'{'light':'switch'}']";
        //        text = String.format("{'devices':'%s'}", text);
        //        "{\"name\":\"Netherlands\",\"cities\":[\"Amsterdam\", \"Tamassint\"]}"

        //
        //        HashMap<String, String> map = new HashMap<>();
        //        map.put("new", "one");

        //        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
        //
        //        Request request = new Request("POST", "/");
        //        request.setJsonEntity(body.toString());
        //        Response response = restClient.performRequest(request);
        //
        //
        //        restClient.performRequestAsync(request, new ResponseListener() {
        //            @Override
        //            public void onSuccess(Response response) {
        //
        //                try {
        //                    String responseBody = EntityUtils.toString(response.getEntity());
        //                    JsonObject jsonObject = convertHttpEntityToJsonObject(responseBody);
        //                    exchange.getMessage().setBody(extractID(jsonObject));
        //                } catch (IOException e) {
        //                    exchange.setException(e);
        //                }
        //            }
        //
        //            @Override
        //            public void onFailure(Exception e) {
        //
        //            }
        //        });
        //        BulkResponse result = esClient.bulk(br.build());
        //        BulkRequest request = new BulkRequest();
        //        request.add(new IndexRequest("posts").id("1")
        //                .source(XContentType.JSON,"field", "foo"));

    }

//    private JsonObject convertHttpEntityToJsonObject(String httpResponse) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // Convert JSON content to Map<String, Object>
//        Map<String, Object> map = objectMapper.readValue(httpResponse, new TypeReference<>() {
//        });
//        // convert to JsonObject
//        return new JsonObject(map);
//    }

//    String extractID(JsonObject doc) {
//        return doc.getString("_id");
//    }

    @Override
    public void configure() throws Exception {
        from("paho:devices")
                //                 .process(new IndexProcessing())
                //.log("toLog")

                .log("Message before marshalling is ${body}")
                // transform this to the proper Java object to avoid dealing with wrong string format
                .unmarshal().json(Pojo.class)
                .process(this::process)
                // marshall properly to Json - mandatory as the component is using REST and JSON under the hood
                //                .marshal().json()
                //                .log("Message after marshalling is : ${body}")
                .transform(body()).setBody(constant("Hello world"))
                .to("elasticsearch-rest-client:docker-cluster?hostAddressesList={{elasticsearch.host}}&operation=INDEX_OR_UPDATE&indexName=devices");

        //        from("direct:search")
        //                .to("elasticsearch://cheese?operation=Search&indexName=heyyou");
        //&certificatePath={{elasticsearch.certificate}}
    }

    //exchange.getMessage().setHeader(ElasticsearchConstants.PARAM_INDEX_NAME, new IndexRequest.Builder<String>().index("light"));
    //exchange.getMessage().setHeader(ElasticsearchConstants.PARAM_OPERATION, ElasticsearchOperation.Index);
    //exchange.getMessage().setBody(new IndexRequest.Builder<String>().document("smth"));
    //?brokerUrl=tcp://{{artemis.host}}:{{artemis.port.mqtt}}
    //    {{elasticsearch.host}}:{{elasticsearch.port.api.binary}}
    //    -Dartemis.host=localhost artemis.port.mqtt=1883  -Delasticsearch.host=localhost -Delasticsearch.port.api.binary=9300
    //    ?hostAddresses={{elasticsearch.host}}:{{elasticsearch.port.api.binary}}?operation=Index

}
