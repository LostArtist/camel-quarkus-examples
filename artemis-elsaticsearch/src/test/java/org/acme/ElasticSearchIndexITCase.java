package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.acme.resource.CustomArtemisTestResource;
import org.acme.resource.ElasticSearchTestResource;
import org.apache.camel.test.junit5.params.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(CustomArtemisTestResource.class)
@QuarkusTestResource(ElasticSearchTestResource.class)
public class ElasticSearchIndexITCase {

    @Test
    void testIndex() {
        System.out.println("The test goes here");
    }

}
