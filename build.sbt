ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "spark-redis-etl",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.1",
      "org.apache.spark" %% "spark-sql" % "3.5.1",
      "com.redislabs" %% "spark-redis" % "3.1.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
      "com.typesafe" % "config" % "1.4.3", // para configuraciones .conf
      "com.crealytics" %% "spark-excel" % "0.13.5",


)
  )
