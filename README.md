
# IntelliJ IDEA Plugin: Serializer Toolbox

[![Build Status](https://travis-ci.org/nitram509/intellij-idea-plugin-serializer-toolbox.svg?branch=master)](https://travis-ci.org/nitram509/intellij-idea-plugin-serializer-toolbox)

## Purpose

This toolbox will generate "ready to use" JSON serializer code for you.
It supports [Jackson's Streaming API](http://wiki.fasterxml.com/JacksonStreamingApi)

**Why?**
There are many JSON serializer and de-serializer libraries available.
Most of them use reflection to serialize your objects.
In most cases and in most environment (e.g. Spring Boot), this works
very well.
But sometimes one want to fine tune for [best 'manual' performance](https://github.com/eishay/jvm-serializers/wiki)
or can't use pre-configured [HttpMessageConverter](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/converter/HttpMessageConverter.html).
In such cases one want to manual serialize a class.


### manual run

```
gradle runIdea
```
