Author: Christos KK Loverdos <loverdos@gmail.com>
Blog:   http://blog.ckkloverdos.com
URL:    http://code.google.com/p/jbootstrap/
M2Repo: http://ckkl-core.svn.sourceforge.net/svnroot/ckkl-core/m2repo

Please do NOT email bug reports or feature requests.

Built by Maven 2.0.7.

Have you been tired of continuously (re)adjusting the CLASSPATH when
developing a new application/library? Have you been tired of issuing
ever increasing "export CLASSPATH/set CLASSPATH" statements in your
shell executable that fires-up your application?

Then jbootstrap can take your headaches away. Just gather all your JAR
dependencies under one directory and run your application by simply
specifying this directory. jbootstrap will pick up the JARs, create an
appropriate classloader and fire-up your application in a fraction of
time.

Basic Usage:
java -jar jbootstrap-x.y.z.jar -lib ./lib my.main.App arg1 arg2 ...

In case your jars are in more than one directories:
java -jar jbootstrap-x.y.z.jar -lib path-to-lib1 -lib path-to-lib2 my.main.App arg1 arg2 ...
