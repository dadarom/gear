
## prepare

### local install
1. [gearpump-shaded-repo](https://github.com/gearpump/gearpump-shaded-repo)
2. [akka-data-replication](https://github.com/patriknw/akka-data-replication)

`
sbt compile
sbt package
mvn install:install-file -Dfile=akka-data-replication_2.11-0.11.jar -DgroupId=com.github.patriknw -DartifactId=akka-data-replication_2.11 -Dversion=0.11 -Dpackaging=jar
`

### local jar install
[add-jars-to-maven](https://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them#364188)
[maven-install-plugin](http://maven.apache.org/plugins/maven-install-plugin/usage.html)
[scala compile](http://icejoywoo.github.io/2016/11/25/maven-examples.html)

### .gitignore
[setting]-[file types]: .gitignore, __ext-lib jars:__

* akka-data-replication_2.11-0.7.jar
* gearpump-shaded-akka-kryo_2.11-0.3.2.jar
* gearpump-shaded-gs-collections-6.2.0.jar
* gearpump-shaded-guava-16.0.1.jar
* gearpump-shaded-metrics-graphite-3.0.2.jar

### probolems
1. __akka.testkit.TestProbe: __ change akka version from 2.3.6 to 2.3.8
2. __daemon test TestUtil compile error__
* module core test added to src root
* OR: copy class(TestUtil/MasterHarness) into daemon test io.gearpump.cluster
* OR: copy class(TestUtil/MasterHarness) into core src root's io.gearpump.cluster

### quickstart
1. master cluster into MasterProxy
2. workers
3. submit app 