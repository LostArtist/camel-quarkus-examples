package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.acme.resource.CustomArtemisTestResource;
import org.acme.resource.ElasticSearchTestResource;

@QuarkusIntegrationTest
@QuarkusTestResource(CustomArtemisTestResource.class)
@QuarkusTestResource(ElasticSearchTestResource.class)
public class ElasticSearchTestIT extends ElasticSearchTest {
}
