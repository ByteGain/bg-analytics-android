package com.bytegain.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.annotation.Config.NONE;

import android.util.Log;
import com.bytegain.analytics.integrations.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class LoggerTest {

  @Test
  public void verboseLevelLogsEverything() {
    Logger logger = Logger.with(Analytics.LogLevel.VERBOSE);

    logger.debug("foo");
    logger.info("bar");
    logger.verbose("qaz");
    logger.error(null, "qux");

    assertThat(ShadowLog.getLogs()).hasSize(4);
  }

  @Test
  public void verboseMessagesShowInLog() {
    Logger logger = Logger.with(Analytics.LogLevel.VERBOSE);

    logger.verbose("some message with an %s", "argument");

    assertThat(ShadowLog.getLogs()) //
        .containsExactly(
            new LogItemBuilder() //
                .type(Log.VERBOSE) //
                .msg("some message with an argument") //
                .build());
  }

  @Test
  public void debugMessagesShowInLog() {
    Logger logger = Logger.with(Analytics.LogLevel.DEBUG);

    logger.debug("some message with an %s", "argument");

    assertThat(ShadowLog.getLogs()) //
        .containsExactly(
            new LogItemBuilder() //
                .type(Log.DEBUG) //
                .msg("some message with an argument") //
                .build());
  }

  @Test
  public void infoMessagesShowInLog() {
    Logger logger = Logger.with(Analytics.LogLevel.INFO);

    logger.info("some message with an %s", "argument");

    assertThat(ShadowLog.getLogs()) //
        .containsExactly(
            new LogItemBuilder() //
                .type(Log.INFO) //
                .msg("some message with an argument") //
                .build());
  }

  @Test
  public void errorMessagesShowInLog() throws Exception {
    Logger logger = Logger.with(Analytics.LogLevel.DEBUG);
    Throwable throwable = new AssertionError("testing");
    logger.error(throwable, "some message with an %s", "argument");

    assertThat(ShadowLog.getLogs()) //
        .containsExactly(
            new LogItemBuilder() //
                .type(Log.ERROR) //
                .throwable(throwable) //
                .msg("some message with an argument") //
                .build());
  }

  @Test
  public void subLog() throws Exception {
    Logger logger = Logger.with(Analytics.LogLevel.DEBUG).subLog("foo");

    logger.debug("some message with an %s", "argument");

    assertThat(ShadowLog.getLogs()) //
        .containsExactly(
            new LogItemBuilder() //
                .tag("Analytics-foo") //
                .type(Log.DEBUG) //
                .msg("some message with an argument") //
                .build());
  }

  static class LogItemBuilder {
    private int type;
    private String tag = "Analytics"; // will be the default tag unless explicitly overridden
    private String msg;
    private Throwable throwable;

    public LogItemBuilder type(int type) {
      this.type = type;
      return this;
    }

    public LogItemBuilder tag(String tag) {
      this.tag = tag;
      return this;
    }

    public LogItemBuilder msg(String msg) {
      this.msg = msg;
      return this;
    }

    public LogItemBuilder throwable(Throwable throwable) {
      this.throwable = throwable;
      return this;
    }

    public ShadowLog.LogItem build() {
      return new ShadowLog.LogItem(type, tag, msg, throwable);
    }
  }
}
