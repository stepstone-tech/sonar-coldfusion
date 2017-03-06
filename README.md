# SonarQube ColdFusion Plugin

A [SonarQube plugin](http://www.sonarqube.org/) for analyzing ColdFusion code, based on the [CFLint library](https://github.com/cflint/CFLint).

## Installation

1. Download the JAR file from the [releases section](https://github.com/stepstone-tech/sonar-coldfusion/releases) or build it yourself by cloning the code and running `mvn install`.
1. Copy `sonar-coldfusion-plugin-{version}.jar` to `<sonarqube dir>/extensions/plugins`.
1. Restart SonarQube.

## Running

Follow the instructions for [analyzing code with SonarQube Scanner](http://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner). The ColdFusion plugin will automatically discover and analyze `.cfc` and `.cfm` files.

## Parameters tuning

If you encounter log output indicating, that the Compute Engine of SonarQube has insufficient memory, similar to:

```
2016.06.22 16:17:43 INFO  ce[o.s.s.c.t.CeWorkerCallableImpl] Execute task | project=ApplyNowModule | type=REPORT | id=AVV4eUIgcn4uboqEX1C3
java.lang.OutOfMemoryError: GC overhead limit exceeded
Dumping heap to java_pid8400.hprof ...
Heap dump file created [565019912 bytes in 6.373 secs]
```

you'll need to increase heap memory on the server, in `<sonarqube dir>/conf/sonar.properties`:

```
sonar.ce.javaOpts=-Xmx2g -Xms128m -XX:+HeapDumpOnOutOfMemoryError
```

2GB might be enough, or perhaps your code base warrants more.

## License

Copyright 2016 StepStone GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
