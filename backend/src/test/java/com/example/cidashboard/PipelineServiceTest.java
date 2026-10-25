package com.example.cidashboard;
import com.example.cidashboard.service.PipelineService; import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.*;
class PipelineServiceTest {
 @Test void seededRunsAreDemoAndRunCreatesClearlySimulatedEntry(){var service=new PipelineService();var before=service.all().size();var run=service.run("feature/test","Student");assertThat(service.all()).hasSize(before+1);assertThat(run.mode()).isEqualTo("DEMO");assertThat(run.status()).isEqualTo("SIMULATED");assertThat(run.stages()).allMatch(s->s.status().equals("SIMULATED"));}
 @Test void unknownRunIsEmpty(){assertThat(new PipelineService().get("missing")).isEmpty();}
}
