lolivimson[![Build Status](https://travis-ci.org/kamichidu/java-lolivimson.svg?branch=master)](https://travis-ci.org/kamichidu/java-lolivimson)
========================================================================================================================
lolivimson is for reading/writing vim evaluatable string from java.
Supported java versions are 6, 7 and 8.


What is lolivimson
------------------------------------------------------------------------------------------------------------------------
lolivimson is:

* Zero-dependency (does not rely on other packages beyond JDK)
* Open Source (The MIT Licence)


How to use via maven
------------------------------------------------------------------------------------------------------------------------
Write this to your pom.

```xml
<project>
    <repositories>
        <repository>
            <id>lolivimson-repository</id>
            <url>https://raw.githubusercontent.com/kamichidu/java-lolivimson/mvn-repo/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>jp.michikusa.chitose</groupId>
            <artifactId>lolivimson</artifactId>
            <version>0.0.0</version>
        </dependency>
    </dependencies>
</project>
```


Example
------------------------------------------------------------------------------------------------------------------------
The API is similar to [jackson](http://jackson.codehaus.org/).

```java
try(VimsonGenerator g= new VimsonGenerator(new FileOutputStream("path/to/output.file")))
{
    g.writeStartDictionary();
    g.writeStringField("key", "value");
    g.writeEndDictionary();
}
catch(IOException e)
{
    ...
}
```
