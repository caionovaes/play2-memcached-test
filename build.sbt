name := """cache-test"""
organization := "novaes.caio"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  cacheApi,
  filters,
  "com.github.mumoshu" %% "play2-memcached-play26" % "0.9.0"
)
