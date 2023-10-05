# Use GraalPy in a Java embedding with JBang

This repository makes it easy to create a [JBang](https://www.jbang.dev/) app that uses GraalPy & Java.

See our example graalpy.java for a template. You can use `PIP` comments to add packages. Native image builds
should just work. Modify the main method to suit your use case, the other two classes in the file are just
helpers that you should be able to ignore.

https://github.com/timfel/graalpy-jbang/blob/ad280678b60a15984f01f3dbcc796a3bb365e092/src/test/resources/graalpy.java#L1-L5
https://github.com/timfel/graalpy-jbang/blob/ad280678b60a15984f01f3dbcc796a3bb365e092/src/test/resources/graalpy.java#L48-L69
