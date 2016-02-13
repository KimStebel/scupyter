enablePlugins(ScalaJSPlugin)

name := "scupyter"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-Xmax-classfile-name","72")

scalaJSUseRhino in Global := false

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.2"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.8"
