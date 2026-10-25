package com.example.cidashboard;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.test.web.servlet.MockMvc; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class ApiControllerTest {
 @Autowired MockMvc mvc;
 @Test void summaryExposesDemoMode() throws Exception {mvc.perform(get("/api/dashboard/summary")).andExpect(status().isOk()).andExpect(jsonPath("$.dataMode").value("DEMO"));}
 @Test void unknownPipelineIs404() throws Exception {mvc.perform(get("/api/pipelines/missing")).andExpect(status().isNotFound());}
 @Test void runRequiresValidFields() throws Exception {mvc.perform(post("/api/pipelines/run").contentType("application/json").content("{} ")).andExpect(status().isBadRequest());}
 @Test void runReturnsAcceptedAndSimulatedStatus() throws Exception {mvc.perform(post("/api/pipelines/run").contentType("application/json").content("{\"branch\":\"main\",\"triggeredBy\":\"Student\"}")).andExpect(status().isAccepted()).andExpect(jsonPath("$.status").value("SIMULATED"));}
 @Test void productEndpointReturnsProduct() throws Exception {mvc.perform(get("/api/products/p-101")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Build Monitor"));}
 @Test void orderEndpointRejectsUnknownProduct() throws Exception {mvc.perform(post("/api/orders").contentType("application/json").content("{\"productId\":\"missing\",\"quantity\":1}")).andExpect(status().isUnprocessableEntity());}
 @Test void orderEndpointRejectsInvalidQuantity() throws Exception {mvc.perform(post("/api/orders").contentType("application/json").content("{\"productId\":\"p-101\",\"quantity\":0}")).andExpect(status().isBadRequest());}
}
