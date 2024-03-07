name := "pfs-back-end"

scalaVersion := "2.13.11"

val akkaVersion = "2.6.21"
val akkaHttpVersion = "10.2.10"
val jacksonVersion = "2.15.2"
val swaggerVersion = "2.2.15"

//resolvers ++= Resolver.sonatypeOssRepos("snapshots")

val swaggerDependencies = Seq(
  "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.0.0",
  "com.github.swagger-akka-http" %% "swagger-akka-http-with-ui" % "2.6.0",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.11.0",
  "com.github.swagger-akka-http" %% "swagger-enumeratum-module" % "2.8.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "io.swagger.core.v3" % "swagger-jaxrs2-jakarta" % swaggerVersion
)

libraryDependencies ++= Seq(
  "pl.iterators" %% "kebs-spray-json" % "1.9.5",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.megard" %% "akka-http-cors" % "1.1.3",
  "org.slf4j" % "slf4j-simple" % "2.0.7",
  "com.github.cb372" %% "scalacache-core" % "0.28.0",
  "com.github.cb372" %% "scalacache-guava" % "0.28.0"
) ++ swaggerDependencies
