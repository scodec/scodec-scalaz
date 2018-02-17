scodecModule := "scodec-scalaz"

enablePlugins(ScodecPrimaryModuleJVMSettings)

contributors ++= Seq(Contributor("mpilquist", "Michael Pilquist"), Contributor("pchiusano", "Paul Chiusano"))

rootPackage := "scodec.interop.scalaz"

libraryDependencies ++= Seq(
  "org.scodec" %% "scodec-core" % "1.10.3",
  "org.scalaz" %% "scalaz-core" % "7.1.12",
  "org.scalaz" %% "scalaz-concurrent" % "7.1.12"
)

OsgiKeys.exportPackage := Seq("scodec.interop.scalaz;version=${Bundle-Version}")

OsgiKeys.importPackage := Seq(
  """scodec.bits.*;version="$<range;[==,=+);$<@>>"""",
  """scodec.*;version="$<range;[==,=+);$<@>>"""",
  """scala.*;version="$<range;[==,=+);$<@>>"""",
  """scalaz.*;version="$<range;[==,=+);$<@>>"""",
  "*"
)

mimaBinaryIssueFilters := Seq(
)
