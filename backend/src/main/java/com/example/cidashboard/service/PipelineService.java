package com.example.cidashboard.service;
import com.example.cidashboard.model.PipelineRun; import org.springframework.stereotype.Service; import java.time.Instant; import java.util.*; import java.util.concurrent.CopyOnWriteArrayList;
@Service public class PipelineService {
 private final List<PipelineRun> runs = new CopyOnWriteArrayList<>();
 private static final List<String> STAGES = List.of("Source checkout","Dependency install","Compile & build","Unit tests","Integration tests","Contract tests","Test reports","Pipeline complete");
 public PipelineService(){ runs.add(demo("run-1042","a83f21c","main","Maya Chen","PASSED",42,0,96,Instant.now().minusSeconds(3600))); runs.add(demo("run-1041","c91d07e","feature/api","Noah Patel","FAILED",39,3,81,Instant.now().minusSeconds(10800))); runs.add(demo("run-1040","4be52af","main","Maya Chen","PASSED",42,0,104,Instant.now().minusSeconds(21600))); }
 private PipelineRun demo(String id,String hash,String branch,String who,String status,int pass,int fail,long sec,Instant at){var s=STAGES.stream().map(n->new PipelineRun.Stage(n,"PASSED",sec/STAGES.size(),"Demonstration result")).toList();return new PipelineRun(id,hash,branch,who,status,pass,fail,sec,at,"DEMO",s);}
 public List<PipelineRun> all(){return List.copyOf(runs);} public Optional<PipelineRun> get(String id){return runs.stream().filter(r->r.id().equals(id)).findFirst();}
 public PipelineRun run(String branch,String by){var s=STAGES.stream().map(n->new PipelineRun.Stage(n,"SIMULATED",0,"Controlled demonstration stage; no remote build was run")).toList();var r=new PipelineRun("demo-"+UUID.randomUUID().toString().substring(0,8),"local",branch,by,"SIMULATED",0,0,0,Instant.now(),"DEMO",s);runs.add(0,r);return r;}
}
