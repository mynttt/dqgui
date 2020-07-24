---
layout: article
title: "Building DQGUI"
category: doc
---

DQGUI is a modular Gradle project that includes a gradle wrapper to guarantee that the correct Gradle version is used.

To build the entire project and all its sub modules simply run ```gradle build``` in the root project. 

The build process will fetch all required dependencies, generate documentation, build the sub modules, run tests and a static analysis and output the two compiled artifacts into the root projects ```bin/``` folder.

### Requirements

Due to the project having a Java 8 requirement, JavaFX is not bundled with the finished artifacts as it is the case with JavaFX >= Java 11 builds.

There is a hard dependency on JavaFX being supplied by the JDK. This is currently the case for the Oracle Java 8 JDK. OpenJDK 8 allows to install the openjfx module on Linux that also provides the JavaFX runtime.

This however does not work on windows. To build this project a Java JDK 8 that supplies JavaFX is required.

If you want to upgrade this project to Java 11+ you'll need to add the [OpenJFX dependencies](https://openjfx.io/) to the build script, this will remove the hard dependency on a JDK that supplies JavaFX.

This is due to Oracles change in policy to not include JavaFX within the JDK anymore.

### Core Modules

| Module Name   | Description  |
| ------------- |:-------------:|
| dqgui-framework | Custom GUI framework to abstract JavaFX boilerplate|
| dqgui-execution      | Shared execution relevant module, contains database abstraction layer, remote data classes and execution metadata classes      | 
| dqgui-database-support | Contains database support that is not relevant to execution i.e. repository and GUI support |
| dqgui-core | The actual DQGUI |
| dqgui-remote | The DQGUI remote execution server|

### Engine Implementations

| Module Name   | Description  |
| ------------- |:-------------:|
| database-postgres | Postgres with repository support |
| database-mongo | MongoDB without repository support |

### DQGUI and IQM4HD

Currently IQM4HD is supplied via a flat dir repository and included in the fat jar build.

To update the version of IQM4HD used you will have to replace the iqm4hd-single-dqgui.jar within the `libs` folder in the root project.

DQGUI depends on the following classes from the iqm4hd-single project.

```
de.mvise.iqm4hd.client.RServiceRservImpl
de.mvise.iqm4hd.client.RConn
de.mvise.iqm4hd.client.DocumentSetIterator
de.mvise.iqm4hd.client.DatabaseEntryImpl
de.mvise.iqm4hd.client.ResultSetIterator

All ANTLR auto generated parser classes and the ANTLR dependency.
```

If you build the iqm4hd-single-dqgui.jar the following dependencies should be changed from `compile` to `implementation` to prevent them from popping up in the dqgui jar.

```gradle
// DQGUI uses tinylog2 as logging framework and thus includes a tinylog2-slf4j binding. The slf4j implementation will cause ambiguities with slf4j binding initialization on startup.
compile group: 'org.slf4j', name: 'slf4j-simple', version: '1.6.1'

// Included in engine implementations already
compile group: 'org.mongodb', name: 'mongo-java-driver', version: '3.8.2'
compile group: 'org.postgresql', name: 'postgresql', version: '42.2.5'

// Bloat, not required by the iqm4hd-single source we received
compile group: 'org.springframework.data', name: 'spring-data-mongodb', version: '2.1.0.RELEASE'
```

You can then change the main jar task to:

```gradle
jar {
    manifest {
        attributes "Main-Class": "de.mvise.iqm4hd.client.ClientMain"
    }
 
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

This will guarantee that the fat jar from the original project is still built with all dependencies.

Creating a second jar task defined as:

```gradle
task dqguiJar(type: Jar) {
	appendix = "dqgui"
	
	from sourceSets.main.output
	
	from {
		configurations.compile.collect { it.isDirectory() ? it : zipTree(it) }
	}
}

jar.dependsOn dqguiJar
```

Will create the iqm4hd-single-dqgui.jar that should be used with the dqgui build. It is stripped of all `implementation` dependencies.