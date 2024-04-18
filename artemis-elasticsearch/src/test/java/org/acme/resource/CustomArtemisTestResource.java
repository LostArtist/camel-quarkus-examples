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

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomArtemisTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "quay.io/artemiscloud/activemq-artemis-broker";
    private static final String AMQ_USER = "admin";
    private static final String AMQ_PASSWORD = "admin";
    private static final String AMQ_JOLOKIA = "\"--relax-jolokia\"";
    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
                .withExposedPorts(61616, 1883, 8161)
                .withEnv("AMQ_USER", AMQ_USER)
                .withEnv("AMQ_PASSWORD", AMQ_PASSWORD)
                .withEnv("AMQ_EXTRA_ARGS", AMQ_JOLOKIA);

        //container.addEnv("AMQ_EXTRA_ARGS", AMQ_JOLOKIA);
        container.start();

        return Map.of(
                "artemis.host", container.getHost(),
                "artemis.admin.http", String.valueOf(container.getMappedPort(8161)),
                "artemis.port.mqtt", String.valueOf(container.getMappedPort(1883)),
                "artemis.admin.username", AMQ_USER,
                "artemis.admin.password", AMQ_PASSWORD);
    }

    @Override
    public void stop() {

    }
}
