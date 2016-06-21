# Installation
1. Execute ``mvn install`` in ``cflint-sonar-bridge`` to build module.
1. Copy ``cflint-sonar-bridge\target\cflint-sonar-bridge-{version}.jar`` to ``sonarqube-5.5\extensions\plugins``.
1. Start sonar.
1. Open page http://localhost:9000/settings?category=coldfusion and set absolute path to ``CFlint.jar`` in field ``CFlint jar``.
1. Done :-)


# Parameters tuning
If you see something like this in sonar logs:

```
2016.06.22 16:17:43 INFO  ce[o.s.s.c.t.CeWorkerCallableImpl] Execute task | project=ApplyNowModule | type=REPORT | id=AVV4eUIgcn4uboqEX1C3
java.lang.OutOfMemoryError: GC overhead limit exceeded
Dumping heap to java_pid8400.hprof ...
Heap dump file created [565019912 bytes in 6.373 secs]
```

You need to increase memory size in `sonarqube-5.5\conf\sonar.properties`:

```sonar.ce.javaOpts=-Xmx2g -Xms128m -XX:MaxPermSize=160m -XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true```