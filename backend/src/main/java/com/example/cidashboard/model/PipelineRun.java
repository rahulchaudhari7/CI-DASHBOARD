package com.example.cidashboard.model;
import java.time.Instant; import java.util.List;
public record PipelineRun(String id, String commit, String branch, String triggeredBy, String status, int passed, int failed, long durationSeconds, Instant timestamp, String mode, List<Stage> stages) {
 public record Stage(String name, String status, long durationSeconds, String detail) {}
}
