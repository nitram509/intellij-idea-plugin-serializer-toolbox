package org.example.testcases;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayOutputStream;

import static org.example.testcases.BasicTypesSerializerTest.asString;
import static org.example.testcases.BasicTypesSerializerTest.createBasicTypesObject;

public class BasicTypesDatabindSerializerTest {

  private BasicTypesDatabindSerializer serializer;

  @Before
  public void setUp() throws Exception {
    serializer = new BasicTypesDatabindSerializer();
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
}