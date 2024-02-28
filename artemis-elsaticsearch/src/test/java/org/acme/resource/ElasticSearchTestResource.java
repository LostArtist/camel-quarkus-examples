package org.acme.resource;

import java.util.Map;

import com.github.dockerjava.api.command.CreateContainerCmd;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

// podman run --rm --net elastic -p 9200:9200 -p 9300:9300 -it -m 4GB -e "discovery.type=single-node" -e ELASTIC_PASSWORD=changeme1 --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:8.12.0
// podman  exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token -s kibana
// podman run --rm --net elastic -p 5601:5601 --name kib01 docker.elastic.co/kibana/kibana:8.12.0
public class ElasticSearchTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:8.12.0";
    private static final String ELASTIC_USER_NAME = "elastic";
    private static final String ELASTIC_PASSWORD = "changeme1";
    private GenericContainer<?> container;

    private Network network = Network.newNetwork();

    private void createContainer(CreateContainerCmd cmd) {
        cmd.withHostName("elasticsearch");
        cmd.withName("elasticsearch");

    }

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
                .withExposedPorts(9200, 9300)
                .withEnv("discovery.type", "single-node")
                .withEnv("ELASTIC_PASSWORD", ELASTIC_PASSWORD)
                .withNetwork(network)
                .withCreateContainerCmdModifier(this::createContainer);

        container.start();

        return Map.of(
                "elasticsearch.host", container.getHost(),
                "elasticsearch.port.api.http", String.valueOf(container.getMappedPort(9200)),
                "elasticsearch.port.api.binary", String.valueOf(container.getMappedPort(9300)),
                "elasticsearch.username", ELASTIC_USER_NAME,
                "elasticsearch.password", ELASTIC_PASSWORD);

    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
