package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.acme.resource.CustomPahoTestResource;
import org.acme.resource.ElasticSearchTestResource;

@QuarkusIntegrationTest
@QuarkusTestResource(CustomPahoTestResource.class)
@QuarkusTestResource(ElasticSearchTestResource.class)
public class ElasticSearchTestIT extends ElasticSearchTest {
}
