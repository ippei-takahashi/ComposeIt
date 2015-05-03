scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.1"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

initialCommands += "import phi._, Phi._, PhiTransformer._"

