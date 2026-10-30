package com.github.devnic0las.omnichannel_dispatcher_api.logging;

public class AuditLogger {

  private static AuditLogger auditLogger;

  private AuditLogger() {}

  public  static synchronized AuditLogger getInstance() {
    if (auditLogger == null) {
      auditLogger = new AuditLogger();
    }
    return auditLogger;
  }
  public void log(String message) {
    System.out.println("[AUDIT LOG] " + message);
  }

}
