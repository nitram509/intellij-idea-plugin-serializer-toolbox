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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;

import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;

public class BasicTypesDeSerializer {

  public BasicTypes deserialize(InputStream inputStream) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    try (JsonParser jp = jsonFactory.createParser(inputStream)) {
      return readObject(jp);
    }
  }

  private BasicTypes readObject(JsonParser jp) throws IOException {
    BasicTypes basicTypes = new BasicTypes();
    for (JsonToken jsonToken; (jsonToken = jp.nextToken()) != null && (jsonToken != END_OBJECT); ) {
      if (FIELD_NAME != jsonToken) continue;
      final String fieldName = jp.getCurrentName();
      switch (fieldName) {
        case "aString":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aString = jp.getText();
          break;
        case "aBoolean":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aBoolean = jp.getBooleanValue();
          break;
        case "aFloat":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aFloat = jp.getFloatValue();
          break;
        case "aDouble":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aDouble = jp.getDoubleValue();
          break;
        case "aInt":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aInt = jp.getIntValue();
          break;
        case "aShort":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aShort = jp.getShortValue();
          break;
        case "aByte":
          jsonToken = jp.nextToken(); // read value
          basicTypes.aByte = jp.getByteValue();
          break;
        default:
          // decide what to do;
      }
    }
    return basicTypes;
  }

}
