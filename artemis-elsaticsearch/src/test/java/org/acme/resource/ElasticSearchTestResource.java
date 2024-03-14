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

//// podman run --rm --net elastic -p 9200:9200 -p 9300:9300 -it -m 4GB -e "discovery.type=single-node" -e ELASTIC_PASSWORD=changeme1 --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:8.12.0
//// podman  exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token -s kibana
//// podman run --rm --net elastic -p 5601:5601 --name kib01 docker.elastic.co/kibana/kibana:8.12.0
public class ElasticSearchTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:8.12.0";
    private static final String ELASTIC_USER_NAME = "elastic";
    private static final String ELASTIC_PASSWORD = "changeme1";
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
