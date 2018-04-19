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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.reflect.ClassPath;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author NARH https://github.com/NARH
 *
 */
@Slf4j
public class SerializableChecker {

  public static void checkPackage(final String packageName) throws IOException  {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    ClassPath.from(loader).getTopLevelClasses(packageName).stream()
      .map(info -> info.load())
      .collect(Collectors.toSet())
      .forEach(c->check(c));
  }

  public static void check(Class<?> cls) {
    boolean check1 = isSerializable(cls);
    if(!check1) log.error("{} checking. ... NG  not implements java.io.Serializable.",cls.getName());
    try {
      if(check1 && null != roundtrip((Serializable)cls.newInstance())) log.info("{} cheking. ... OK", cls.getName());
    }
    catch(Exception e) {
      log.error("{} checking. ... NG   not serialization.",cls.getName());
    }
  }

  public static Object roundtrip(Serializable obj)
      throws IllegalAccessException,InstantiationException, NotSerializableException {
    return SerializationUtils.deserialize(SerializationUtils.serialize(obj));
  }

  private static boolean isSerializable(Class<?> cls) {
    try {
      cls.asSubclass(Serializable.class);
      return true;
    }
    catch(ClassCastException e) {
      return false;
    }
  }
}
