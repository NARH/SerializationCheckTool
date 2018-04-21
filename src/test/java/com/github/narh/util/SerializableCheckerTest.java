/*
 * Copyright (c) 2018, NARH https://github.com/NARH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the copyright holder nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.narh.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.github.narh.util.bean.NestedBean;
import com.github.narh.util.bean.NestedUnSerializableBean;
import com.github.narh.util.bean.SimpleBean;
import com.github.narh.util.bean.UnSerializableBean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

/**
 *
 * @author NARH https://github.com/NARH
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SerializableCheckerTest {

  @Mock @SuppressWarnings("rawtypes")
  private Appender mockAppender;

  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @Before @SuppressWarnings("unchecked")
  public void setup() {
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.addAppender(mockAppender);
  }

  @After @SuppressWarnings("unchecked")
  public void teardown() {
    final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAppender(mockAppender);
  }

  @Test @SuppressWarnings("unchecked")
  public void 正常系Test() throws Exception {
    SerializableChecker.check(SimpleBean.class);
    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is("com.github.narh.util.bean.SimpleBean cheking. ... OK"));
  }

  @Test @SuppressWarnings("unchecked")
  public void 入れ子Test() throws Exception {
    SerializableChecker.check(NestedBean.class);
    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is("com.github.narh.util.bean.NestedBean cheking. ... OK"));
  }

  @Test @SuppressWarnings("unchecked")
  public void SerializableをimplementsしていないTest() throws Exception {
    SerializableChecker.check(UnSerializableBean.class);
    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is("com.github.narh.util.bean.UnSerializableBean checking. ... NG  not implements java.io.Serializable."));
  }

  @Test @SuppressWarnings("unchecked")
  public void propertyがSerializableをimplementsしていないTest() throws Exception {
    SerializableChecker.check(NestedUnSerializableBean.class);
    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(Level.INFO));
    assertThat(loggingEvent.getFormattedMessage(), is("com.github.narh.util.bean.NestedUnSerializableBean checking. ... NG   not serialization."));
  }
}
