package org.acme;

import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.apache.camel.test.junit5.params.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(ArtemisTestResource.class)
public class ElasticsearchIndexTest {

    @Test
    void testIndex() {

    }

}
