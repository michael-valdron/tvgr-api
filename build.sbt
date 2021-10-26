name := """tvgr-api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

resolvers += Resolver.jcenterRepo

val playSilhouetteVersion = "7.0.0"
val playSlickVersion = "5.0.0"
val slickVersion = "3.3.3"

libraryDependencies += guice
libraryDependencies += filters
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.mohiva" %% "play-silhouette" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,
  "org.postgresql" % "postgresql" % "42.2.24"
)
