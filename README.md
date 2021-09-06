# Netty.IO Test #1
This test project demonstrates the issue of the `netty-all` package not forcing the version of other netty packages to
get updated.  See [#1657](https://github.com/netty/netty/issues/11657).  

This app does not crash do to the multiple netty versions on the classpath, but there are cases where having multiple
netty.io jars of different versions on the classpath will cause a crash. 

# Building
Run `./gradlew dist`  
Built jar and libs will be in `./app/build/dist`.   
Take note in the `libs/` folder, the multiple versions of netty.io jars. There will be a 4.1.67 `netty-all` jar, and the
others are version 4.1.63.

# Dependency Graph
To print the dependency graph, run `./gradlew app:dependencies`.  
It will print the following for the runtime classpath.
```
runtimeClasspath - Runtime classpath of source set 'main'.
+--- project :lib
|    \--- io.netty:netty-codec-http:4.1.63.Final
|         +--- io.netty:netty-common:4.1.63.Final
|         +--- io.netty:netty-buffer:4.1.63.Final
|         |    \--- io.netty:netty-common:4.1.63.Final
|         +--- io.netty:netty-transport:4.1.63.Final
|         |    +--- io.netty:netty-common:4.1.63.Final
|         |    +--- io.netty:netty-buffer:4.1.63.Final (*)
|         |    \--- io.netty:netty-resolver:4.1.63.Final
|         |         \--- io.netty:netty-common:4.1.63.Final
|         +--- io.netty:netty-codec:4.1.63.Final
|         |    +--- io.netty:netty-common:4.1.63.Final
|         |    +--- io.netty:netty-buffer:4.1.63.Final (*)
|         |    \--- io.netty:netty-transport:4.1.63.Final (*)
|         \--- io.netty:netty-handler:4.1.63.Final
|              +--- io.netty:netty-common:4.1.63.Final
|              +--- io.netty:netty-resolver:4.1.63.Final (*)
|              +--- io.netty:netty-buffer:4.1.63.Final (*)
|              +--- io.netty:netty-transport:4.1.63.Final (*)
|              \--- io.netty:netty-codec:4.1.63.Final (*)
\--- io.netty:netty-all:4.1.67.Final
```
