/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Martin W. Kirst
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.example.testcases;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class BasicTypeArraysSerializer {
  private final JsonFactory jsonFactory = new JsonFactory();

  public void serialize(File targetFile, BasicTypeArrays basicTypeArrays) throws IOException {
    try (JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8)) {
      writeObject(jg, basicTypeArrays);
    }
  }

  public void serialize(OutputStream outputStream, BasicTypeArrays basicTypeArrays) throws IOException {
    try (JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8)) {
      writeObject(jg, basicTypeArrays);
    }
  }

  private void writeArray_String(JsonGenerator jg, String[] array) throws IOException {
    jg.writeStartArray();
    for (String val : array) {
      jg.writeObject(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_boolean(JsonGenerator jg, boolean[] array) throws IOException {
    jg.writeStartArray();
    for (boolean val : array) {
      jg.writeBoolean(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_float(JsonGenerator jg, float[] array) throws IOException {
    jg.writeStartArray();
    for (float val : array) {
      jg.writeNumber(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_double(JsonGenerator jg, double[] array) throws IOException {
    jg.writeStartArray();
    for (double val : array) {
      jg.writeNumber(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_int(JsonGenerator jg, int[] array) throws IOException {
    jg.writeStartArray();
    for (int val : array) {
      jg.writeNumber(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_char(JsonGenerator jg, char[] array) throws IOException {
    jg.writeStartArray();
    for (char val : array) {
      jg.writeObject(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_short(JsonGenerator jg, short[] array) throws IOException {
    jg.writeStartArray();
    for (short val : array) {
      jg.writeNumber(val);
    }
    jg.writeEndArray();
  }

  private void writeArray_byte(JsonGenerator jg, byte[] array) throws IOException {
    jg.writeStartArray();
    for (byte val : array) {
      jg.writeNumber(val);
    }
    jg.writeEndArray();
  }

  private void writeObject(JsonGenerator jg, BasicTypeArrays basicTypeArrays) throws IOException {
    jg.writeStartObject();
    // write field strings...
    jg.writeFieldName("strings");
    writeArray_String(jg, basicTypeArrays.strings);
    // write field booleans...
    jg.writeFieldName("booleans");
    writeArray_boolean(jg, basicTypeArrays.booleans);
    // write field floats...
    jg.writeFieldName("floats");
    writeArray_float(jg, basicTypeArrays.floats);
    // write field doubles...
    jg.writeFieldName("doubles");
    writeArray_double(jg, basicTypeArrays.doubles);
    // write field ints...
    jg.writeFieldName("ints");
    writeArray_int(jg, basicTypeArrays.ints);
    // write field chars...
    jg.writeFieldName("chars");
    writeArray_char(jg, basicTypeArrays.chars);
    // write field shorts...
    jg.writeFieldName("shorts");
    writeArray_short(jg, basicTypeArrays.shorts);
    // write field bytes...
    jg.writeFieldName("bytes");
    writeArray_byte(jg, basicTypeArrays.bytes);
    // done.
    jg.writeEndObject();
  }
}