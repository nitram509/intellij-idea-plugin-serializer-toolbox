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

public class BasicTypesSerializer {
  public void serialize(File targetFile, BasicTypes basicTypes) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8);
    writeObject(jg, basicTypes);
    jg.close();
  }

  public void serialize(OutputStream outputStream, BasicTypes basicTypes) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8);
    writeObject(jg, basicTypes);
    jg.close();
  }

  private void writeObject(JsonGenerator jg, BasicTypes basicType) throws IOException {
    jg.writeStartObject();
    // write field aString...
    jg.writeFieldName("aString");
    jg.writeString(basicType.aString);
    // write field aBoolean...
    jg.writeFieldName("aBoolean");
    jg.writeBoolean(basicType.aBoolean);
    // write field aFloat...
    jg.writeFieldName("aFloat");
    jg.writeNumber(basicType.aFloat);
    // write field aDouble...
    jg.writeFieldName("aDouble");
    jg.writeNumber(basicType.aDouble);
    // write field aInt...
    jg.writeFieldName("aInt");
    jg.writeNumber(basicType.aInt);
    // write field aShort...
    jg.writeFieldName("aShort");
    jg.writeNumber(basicType.aShort);
    // write field aByte...
    jg.writeFieldName("aByte");
    jg.writeNumber(basicType.aByte);
    // done.
    jg.writeEndObject();
  }
}
