package io.github.jinganix.ddz.helper.actor;

import io.github.jinganix.peashooter.TraceExecutor;
import io.github.jinganix.peashooter.Tracer;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class VirtualTraceExecutor extends TraceExecutor {

  public VirtualTraceExecutor(Tracer tracer) {
    super(Executors.newVirtualThreadPerTaskExecutor(), tracer);
  }
}
