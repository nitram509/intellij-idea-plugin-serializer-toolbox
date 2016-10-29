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

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTypesDeSerializerTest {

  private BasicTypesDeSerializer deSerializer;

  @Before
  public void setUp() throws Exception {
    deSerializer = new BasicTypesDeSerializer();
  }

  @Test
  public void name() throws Exception {

    String expectedJson = "{" +
        "   \"aString\": \"foobar\"," +
        "   \"aBoolean\": false," +
        "   \"aFloat\": 23.23," +
        "   \"aDouble\": 42.42," +
        "   \"aInt\": 65536," +
        "   \"aShort\": 128," +
        "   \"aByte\": -5" +
        "}";


    BasicTypes deserialized = deSerializer.deserialize(new ByteArrayInputStream(expectedJson.getBytes()));

    BasicTypes basicTypes = BasicTypesSerializerTest.createBasicTypesObject();
    assertThat(deserialized.aString).isEqualTo(basicTypes.aString);
    assertThat(deserialized.aBoolean).isEqualTo(basicTypes.aBoolean);
    assertThat(deserialized.aFloat).isEqualTo(basicTypes.aFloat);
    assertThat(deserialized.aDouble).isEqualTo(basicTypes.aDouble);
    assertThat(deserialized.aInt).isEqualTo(basicTypes.aInt);
    assertThat(deserialized.aShort).isEqualTo(basicTypes.aShort);
    assertThat(deserialized.aByte).isEqualTo(basicTypes.aByte);
  }
}