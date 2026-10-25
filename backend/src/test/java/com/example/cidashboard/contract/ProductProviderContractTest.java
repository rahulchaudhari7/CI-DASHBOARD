package com.example.cidashboard.contract;
import au.com.dius.pact.provider.junit5.*; import au.com.dius.pact.provider.junitsupport.Provider; import au.com.dius.pact.provider.junitsupport.State; import au.com.dius.pact.provider.junitsupport.loader.PactFolder; import org.junit.jupiter.api.*; import org.junit.jupiter.api.extension.ExtendWith; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.boot.test.web.server.LocalServerPort;
@Provider("product-service") @PactFolder("pacts") @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT) class ProductProviderContractTest {
 @LocalServerPort int port;
 @State("product p-101 exists") void productExists() {}
 @TestTemplate @ExtendWith(PactVerificationInvocationContextProvider.class) void verify(PactVerificationContext context){context.verifyInteraction();}
 @BeforeEach void setup(PactVerificationContext context){context.setTarget(new HttpTestTarget("localhost",port,"/"));}
}
