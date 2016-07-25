lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.digitalearns",
  scalaVersion := "2.11.7",
  test in assembly := {}
)

resolvers += "Concurrent Maven Repo" at "http://conjars.org/repo"

lazy val exampleApp = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    assemblyJarName in assembly := "exampleApp.jar",
    test in assembly := {}
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twitter"       %% "scalding-core" % "0.16.0",                       // dependency for twitter scalding
      "org.apache.hadoop" %  "hadoop-core"   % "0.20.2"          % "provided", // dependency for hadoop
      "org.specs2"        %% "specs2"        % "2.3.11"          % "test",     // dependency for scala specs2
      "org.scalatest"     % "scalatest_2.11" % "3.0.0-M16-SNAP6" % "test"
    )
  )
  .settings(
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
      case s if s.endsWith(".class") => MergeStrategy.last
      case s if s.endsWith("project.clj") => MergeStrategy.concat
      case s if s.endsWith(".html") => MergeStrategy.last
      case s if s.endsWith(".dtd") => MergeStrategy.last
      case s if s.endsWith(".xsd") => MergeStrategy.last
      case s if s.endsWith("pom.properties") => MergeStrategy.last
      case s if s.endsWith("pom.xml") => MergeStrategy.last
      case s if s.endsWith(".jnilib") => MergeStrategy.rename
      case s if s.endsWith("jansi.dll") => MergeStrategy.rename
      case s if s.endsWith("properties") => MergeStrategy.filterDistinctLines
      case x => old(x)
    }
  })
  .settings(docker.settings: _*)
  .settings(docker.testSettings(): _*)


