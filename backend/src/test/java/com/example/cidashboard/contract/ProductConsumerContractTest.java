package com.example.cidashboard.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
class ProductConsumerContractTest {
    @Pact(consumer = "order-service", provider = "product-service")
    RequestResponsePact productExists(PactDslWithProvider builder) {
        return builder.given("product p-101 exists")
                .uponReceiving("a product lookup for an order")
                .path("/api/products/p-101").method("GET")
                .willRespondWith().status(200).headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody().stringValue("id", "p-101")
                        .stringType("name", "Build Monitor").numberType("price", 29.99))
                .toPact();
    }

    @Test @PactTestFor(providerName = "product-service", pactMethod = "productExists", pactVersion = PactSpecVersion.V3)
    void orderConsumerCanReadProduct(MockServer server) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(server.getUrl() + "/api/products/p-101")).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Build Monitor", "p-101");
    }
}
