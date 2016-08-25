# SonarQube ColdFusion Plugin

A [SonarQube plugin](http://www.sonarqube.org/) for analyzing ColdFusion code, based on the [CFLint library](https://github.com/cflint/CFLint).

## Installation

1. Execute `mvn install` to build module.
1. Copy `target/sonar-coldfusion-plugin-{version}.jar` to `<sonarqube dir>/extensions/plugins`.
1. Restart SonarQube.
1. Run `sonar-scanner` on your ColdFusion code.

## Parameters tuning

If you see something like this in SonarQube logs:

```
2016.06.22 16:17:43 INFO  ce[o.s.s.c.t.CeWorkerCallableImpl] Execute task | project=ApplyNowModule | type=REPORT | id=AVV4eUIgcn4uboqEX1C3
java.lang.OutOfMemoryError: GC overhead limit exceeded
Dumping heap to java_pid8400.hprof ...
Heap dump file created [565019912 bytes in 6.373 secs]
```

You need to increase memory size in `<sonarqube dir>/conf/sonar.properties`:

```
sonar.ce.javaOpts=-Xmx2g -Xms128m -XX:MaxPermSize=160m -XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true
```

## License

Copyright 2016 StepStone GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
