package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.acme.resource.CustomArtemisTestResource;
import org.acme.resource.ElasticSearchTestResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(CustomArtemisTestResource.class)
@QuarkusTestResource(ElasticSearchTestResource.class)
public class ElasticSearchIndexITCase {

    @Test
    public void testSomeSampleStuff() {
        // TODO add test
        Assertions.assertTrue(true, "You need to add tests");
    }

}
