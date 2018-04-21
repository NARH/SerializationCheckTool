## setup & usage

```
  git clone https://github.com/NARH/SerializationCheckTool.git
  cd SerializationCheckTool
  ./gradlew executableJar
  java -jar build/libs/SerializationChecker.jar com.github.narh.util.bean file://$(pwd)/entity.jar
  23:05:41.151 [main] INFO  c.g.narh.util.SerializableChecker - com.github.narh.util.bean.UnSerializableBean checking. ... NG  not implements java.io.Serializable.
  23:05:41.184 [main] INFO  c.g.narh.util.SerializableChecker - com.github.narh.util.bean.NestedBean cheking. ... OK
  23:05:41.199 [main] INFO  c.g.narh.util.SerializableChecker - com.github.narh.util.bean.NestedUnSerializableBean checking. ... NG   not serialization.
  23:05:41.201 [main] INFO  c.g.narh.util.SerializableChecker - com.github.narh.util.bean.SimpleBean cheking. ... OK
```
