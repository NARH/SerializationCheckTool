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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.reflect.ClassPath;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author NARH https://github.com/NARH
 *
 */
@Slf4j
public class SerializableChecker {

  private static ClassLoader urlClassLoader = null;

  public static void checkPackage(final String packageName) throws IOException  {
    if(log.isTraceEnabled()) log.trace("begin SerializableChecker-{}", "checkPackage");
    ClassLoader loader = getClassLoader();
    Set<Class<?>> classes = ClassPath.from(loader).getTopLevelClasses(packageName).stream()
      .filter(c->-1 == c.getName().indexOf("package-info"))
      .map(info -> info.load())
      .collect(Collectors.toSet());
    if(log.isTraceEnabled()) log.trace("end SerializableChecker-{}", "checkPackage");
    if(log.isDebugEnabled()) log.debug(classes.toString());

    if (null != urlClassLoader) load(classes);

    classes.forEach(c->check(c));
  }

  private static void load(Set<Class<?>> classes) {
    if(log.isTraceEnabled()) log.trace("begin SerializableChecker-{}", "load");
    try {
      for(Class<?>cls:classes) {
        if(log.isDebugEnabled()) log.debug("loading ... {}-{}", urlClassLoader, cls.getName());
        Class.forName(cls.getName(), true, urlClassLoader);
      }
    }
    catch (ClassNotFoundException e) {
      if (log.isErrorEnabled()) log.error(e.getMessage(), e);
    }
    if(log.isTraceEnabled()) log.trace("end SerializableChecker-{}", "load");
  }

  public static void check(Class<?> cls) {
    if(log.isTraceEnabled()) log.trace("begin SerializableChecker-{}", "check");
    boolean check1 = isSerializable(cls);
    if(!check1) log.info("{} checking. ... NG  not implements java.io.Serializable.",cls.getName());
    try {
      if(check1 && null != roundtrip((Serializable)cls.newInstance())) log.info("{} cheking. ... OK", cls.getName());
    }
    catch(Exception e) {
      if(log.isDebugEnabled()) log.debug(e.getMessage(), e);
      log.info("{} checking. ... NG   not serialization.",cls.getName());
    }
    if(log.isTraceEnabled()) log.trace("end SerializableChecker-{}", "check");
  }

  private static URL[] getUrls(String... url) {
    List<URL>  urlList = new ArrayList<>();
    if(null != url && 0 < url.length) {
      for(String u:url) {
        try {
          urlList.add(new URL(u));
        }
        catch(MalformedURLException e) {
          if(log.isErrorEnabled()) log.error(e.getMessage());
          continue;
        }
      }
    }
    return urlList.toArray(new URL[0]);
  }

  public static ClassLoader getClassLoader(final String... url) {
    if(null == urlClassLoader) {
      if(log.isDebugEnabled()) log.debug("loading ... {}", StringUtils.join(url));
      urlClassLoader = (null != url && 0 < url.length)
          ? new URLClassLoader(getUrls(url), ClassLoader.getSystemClassLoader())
          : Thread.currentThread().getContextClassLoader();
    }
    return urlClassLoader;
  }

  public static Object roundtrip(Serializable obj)
      throws IllegalAccessException,InstantiationException, NotSerializableException {
    return deserialize(SerializationUtils.serialize(obj));
  }

  private static boolean isSerializable(Class<?> cls) {
    if(log.isTraceEnabled()) log.trace("begin SerializableChecker-{}", "isSerializable");
    boolean result = false;
    try {
      if(log.isDebugEnabled()) log.debug("serializable implements check ... {}-{}",Thread.currentThread().getName(), cls.getName());
      cls.asSubclass(Serializable.class);
      result = true;
    }
    catch(ClassCastException e) {
      if(log.isDebugEnabled()) log.debug(e.getMessage(), e);
    }
    if(log.isTraceEnabled()) log.trace("end SerializableChecker-{}", "isSerializable");
    return result;
  }

  public static <T> T deserialize(final byte[] objectData) {
    Validate.isTrue(objectData != null, "The byte[] must not be null");
    return deserialize(new ByteArrayInputStream(objectData));
  }

  public static <T> T deserialize(final InputStream inputStream) {
    Validate.isTrue(inputStream != null, "The InputStream must not be null");
    try (ObjectInputStream in = new ClassLoaderAwareObjectInputStream(inputStream, urlClassLoader)) {
      @SuppressWarnings("unchecked")
      final T obj = (T) in.readObject();
      return obj;
    }
    catch (final ClassNotFoundException | IOException ex) {
      throw new SerializationException(ex);
    }
  }
}
