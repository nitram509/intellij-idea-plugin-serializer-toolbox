/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.example.testcases;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JacksonSerializerBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    volatile BasicTypesSerializer serializer = new BasicTypesSerializer();
    volatile BasicTypes basicTypes = createBasicTypesObject();
    volatile BasicTypesDatabindSerializer databindSerializer = new BasicTypesDatabindSerializer();

    static BasicTypes createBasicTypesObject() {
      BasicTypes basicTypes = new BasicTypes();
      basicTypes.aString = "foobar";
      basicTypes.aBoolean = false;
      basicTypes.aFloat = 23.23f;
      basicTypes.aDouble = 42.42d;
      basicTypes.aInt = 65536;
      basicTypes.aShort = 128;
      basicTypes.aByte = -5;
      return basicTypes;
    }
  }

  @Benchmark
  public void jacksonManualStreamSerialization(BenchmarkState state) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    state.serializer.serialize(baos, state.basicTypes);
  }

  @Benchmark
  public void jacksonDatabindingSerialization(BenchmarkState state) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    state.databindSerializer.serialize(baos, state.basicTypes);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(JacksonSerializerBenchmark.class.getSimpleName())
        .forks(1)
        .warmupIterations(2)
        .measurementIterations(5)
        .build();

    new Runner(opt).run();
  }

}