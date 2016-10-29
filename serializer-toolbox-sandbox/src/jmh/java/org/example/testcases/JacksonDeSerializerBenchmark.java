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

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JacksonDeSerializerBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    volatile BasicTypesDeSerializer deserializer = new BasicTypesDeSerializer();
    volatile byte[] json = exampleJson().getBytes();
    volatile BasicTypesDatabindDeSerializer databindDeSerializer = new BasicTypesDatabindDeSerializer();

    static String exampleJson() {
      return "{" +
          "   \"aString\": \"foobar\"," +
          "   \"aBoolean\": false," +
          "   \"aFloat\": 23.23," +
          "   \"aDouble\": 42.42," +
          "   \"aInt\": 65536," +
          "   \"aShort\": 128," +
          "   \"aByte\": -5" +
          "}";

    }
  }

  @Benchmark
  public void jacksonManualStreamDeSerialization(BenchmarkState state) throws IOException {
    state.deserializer.deserialize(new ByteArrayInputStream(state.json));
  }

  @Benchmark
  public void jacksonDatabindingDeSerialization(BenchmarkState state) throws IOException {
    state.databindDeSerializer.deserialize(new ByteArrayInputStream(state.json));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(JacksonDeSerializerBenchmark.class.getSimpleName())
        .forks(1)
        .warmupIterations(2)
        .measurementIterations(5)
        .build();

    new Runner(opt).run();
  }

}