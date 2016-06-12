
# IntelliJ IDEA Plugin: Serializer Toolbox

[![Build Status](https://travis-ci.org/nitram509/intellij-idea-plugin-serializer-toolbox.svg?branch=master)](https://travis-ci.org/nitram509/intellij-idea-plugin-serializer-toolbox)

## Purpose

There are many JSON serializer and de-serializer libraries available.
Most of them use reflection to serialize your objects.
In most cases and in most environment (e.g. Spring Boot), this works
very well.
*BUT* sometimes one want to fine tune for best performance
or can't use pre-configured [HttpMessageConverter](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/converter/HttpMessageConverter.html).
In such cases one want to manual serialize a class.

This toolbox will generate JSON serializer code for you - it just takes a few clicks ;-)

### manual run

```
gradle runIdea
```
