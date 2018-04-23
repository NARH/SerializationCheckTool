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


import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 起動クラス
 *
 * @author NARH https://github.com/NARH
 *
 */
@Slf4j
public class Main {

  public static void main(String... args) throws Exception {
    if(log.isDebugEnabled()) log.debug("args {}", StringUtils.join(args));

    if(null == args || 0 == args.length) {
      log.info(" package name required.");
      System.exit(-1);
    }

    if(log.isTraceEnabled()) log.trace("begin Main-main: {}", "class loader setup");
    if(1 < args.length) {
      String[] jarArgs = new String[args.length -1];
      for(int i = 1; i < args.length; i++) jarArgs[i-1] = args[i];
      if(log.isDebugEnabled()) log.debug("jarArgs {}", StringUtils.join(jarArgs));
      SerializableChecker.getClassLoader(jarArgs);
    }
    if(log.isTraceEnabled()) log.trace("end Main-main: {}", "class loader setup");
    if(log.isTraceEnabled()) log.trace("begin Main-main: {}", "checkPackage");
    SerializableChecker.checkPackage(args[0]);
    if(log.isTraceEnabled()) log.trace("end Main-main: {}", "checkPackage");
  }
}
