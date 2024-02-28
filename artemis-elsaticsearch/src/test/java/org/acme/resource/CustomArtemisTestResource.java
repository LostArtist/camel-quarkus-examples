package org.acme.resource;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

// podman run -e AMQ_EXTRA_ARGS="--relax-jolokia" -e AMQ_USER=admin -e AMQ_PASSWORD=admin -p 61616:61616 -p 8161:8161 -p 1883:1883 quay.io/artemiscloud/activemq-artemis-broker
public class CustomArtemisTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "quay.io/artemiscloud/activemq-artemis-broker";
    private static final String AMQ_USER = "admin";
    private static final String AMQ_PASSWORD = "admin";
    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
                .withExposedPorts(61616, 1883, 8161)
                .withEnv("AMQ_USER", AMQ_USER)
                .withEnv("AMQ_PASSWORD", AMQ_PASSWORD);

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
