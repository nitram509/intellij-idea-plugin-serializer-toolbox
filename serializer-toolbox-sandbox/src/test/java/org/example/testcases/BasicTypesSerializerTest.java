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

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class BasicTypesSerializerTest {

  private static final Charset ASCII = Charset.forName("ASCII");

  private BasicTypesSerializer serializer;

  @Before
  public void setUp() throws Exception {
    serializer = new BasicTypesSerializer();
  }

  @Test
  public void name() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BasicTypes basicTypes = createBasicTypesObject();

    serializer.serialize(baos, basicTypes);

    String expectedJson = "{" +
        "   \"aString\": \"foobar\"," +
        "   \"aBoolean\": false," +
        "   \"aFloat\": 23.23," +
        "   \"aDouble\": 42.42," +
        "   \"aInt\": 65536," +
        "   \"aShort\": 128," +
        "   \"aByte\": -5" +
        "}";

    JSONAssert.assertEquals(expectedJson, asString(baos), false);
  }

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

  static String asString(ByteArrayOutputStream baos) {
    return new String(baos.toByteArray(), ASCII);
  }
}
