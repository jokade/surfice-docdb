//import SonatypeKeys._

lazy val commonSettings = Seq(
  organization := "de.surfice",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.3.1" % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework"),
  resolvers += Resolver.sonatypeRepo("snapshots")
)

lazy val root = project.in(file(".")).
  aggregate(commonJVM,commonJS,pouchdb).
  settings(commonSettings:_*).
  //settings(sonatypeSettings: _*).
  settings(
    name := "surfice-docdb",
    publish := {},
    publishLocal := {}
  )

lazy val common = crossProject.in(file(".")).
  settings(commonSettings:_*).
  //settings(publishingSettings:_*).
  settings(
    name := "surfice-docdb-common",
    libraryDependencies ++= Seq(
      "de.surfice" %%% "surf-core" % "0.1-SNAPSHOT"
    )
  ).
  jvmSettings(
  ).
  jsSettings(
    //preLinkJSEnv := NodeJSEnv().value,
    //postLinkJSEnv := NodeJSEnv().value
  )

lazy val commonJVM = common.jvm
lazy val commonJS = common.js

lazy val pouchdb = project.
  enablePlugins(ScalaJSPlugin).
  dependsOn( commonJS % "compile->compile;test->test" ).
  settings(commonSettings: _*).
  settings(
    name := "surfice-docdb-pouchdb",
    scalaJSStage in Global := FastOptStage
  )

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>https://github.com/jokade/surfice-docdb</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/surfice-docdb</url>
      <connection>scm:git:git@github.com:jokade/surfice-docdb.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jokade</id>
        <name>Johannes Kastner</name>
        <email>jokade@karchedon.de</email>
      </developer>
    </developers>
  )
)
 
