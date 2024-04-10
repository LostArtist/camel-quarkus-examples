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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticSearchTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:8.12.0";
    private static final String ELASTIC_USER_NAME = "elastic";
    private static final String ELASTIC_PASSWORD = "elastic";
    private ElasticsearchContainer container;
    private Path certPath;

    @Override
    public Map<String, String> start() {
        container = new ElasticsearchContainer(DockerImageName.parse(IMAGE_NAME))
                .withPassword(ELASTIC_PASSWORD);
        container.setWaitStrategy(
                new LogMessageWaitStrategy()
                        .withRegEx(".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)")
                        .withStartupTimeout(Duration.ofSeconds(90)));

        container.caCertAsBytes().ifPresent(content -> {
            try {
                certPath = Files.createTempFile("http_ca", ".crt");
                Files.write(certPath, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        container.start();

        final String certPathStr = Optional.ofNullable(certPath).map(Objects::toString).orElse("");

        return Map.of(
                "elasticsearch.host", container.getHost(),
                "elasticsearch.port.api.http", String.valueOf(container.getMappedPort(9200)),
                "elasticsearch.port.api.binary", String.valueOf(container.getMappedPort(9300)),
                "elasticsearch.username", ELASTIC_USER_NAME,
                "elasticsearch.password", ELASTIC_PASSWORD,
                "elasticsearch.certificate", certPathStr);

    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
