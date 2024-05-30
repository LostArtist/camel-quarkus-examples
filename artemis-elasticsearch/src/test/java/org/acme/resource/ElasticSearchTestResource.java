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

package org.acme.resource;

import java.time.Duration;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticSearchTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:8.12.0";
    private ElasticsearchContainer container;

    @Override
    public Map<String, String> start() {
        container = new ElasticsearchContainer(DockerImageName.parse(IMAGE_NAME))
                .withExposedPorts(9200, 9300)
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.enabled", "false");


        container.setWaitStrategy(
                new LogMessageWaitStrategy()
                        .withRegEx(".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)")
                        .withStartupTimeout(Duration.ofSeconds(60)));

        container.start();

        return Map.of(
                "camel.component.elasticsearch-rest-client.host-addresses", "http://" + container.getHost() + ":" + container.getMappedPort(9200));

    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
