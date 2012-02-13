Author: Christos KK Loverdos <loverdos@gmail.com>
URL:    http://code.google.com/p/jbootstrap/

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