name := "aws-upload-sample"

version := "1.0"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.371"
)
