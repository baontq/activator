import sbt._
import PlayProject._
import Keys._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

object SnapBuild {

  def baseVersions: Seq[Setting[_]] = Seq(
    version := {
      // TODO - We don't want to have to run "reload" for new versions....
      val df = new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss")
      df setTimeZone java.util.TimeZone.getTimeZone("GMT")
      // TODO - Add git sha perhaps, because that might help with staleness...
      "1.0-" + (df format (new java.util.Date))
    }
  )

  def formatPrefs = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(IndentSpaces, 2)
  }

  def snapDefaults: Seq[Setting[_]] =
    SbtScalariform.scalariformSettings ++
    Seq(
      organization := "com.typesafe.snap",
      version <<= version in ThisBuild,
      crossPaths := false,
      resolvers += "typesafe-mvn-releases" at "http://repo.typesafe.com/typesafe/releases/",
      resolvers += Resolver.url("typesafe-ivy-releases", new URL("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns),
      // TODO - This won't be needed when SBT 0.13 is released...
      resolvers += Resolver.url("typesafe-ivy-releases", new URL("http://private-repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.ivyStylePatterns),
      scalacOptions <<= (scalaVersion) map { sv =>
        Seq("-unchecked", "-deprecation") ++
          { if (sv.startsWith("2.9")) Seq.empty else Seq("-feature") }
      },
      javacOptions in Compile := Seq("-target", "1.6", "-source", "1.6"),
      javacOptions in (Compile, doc) := Seq("-source", "1.6"),
      libraryDependencies += SnapDependencies.junitInterface % "test",
      scalaVersion := SnapDependencies.scalaVersion,
      scalaBinaryVersion := "2.10",
      ScalariformKeys.preferences in Compile := formatPrefs,
      ScalariformKeys.preferences in Test    := formatPrefs
    )


  def SnapProject(name: String): Project = (
    Project("snap-" + name, file(name))
    settings(snapDefaults:_*)
  )

  
  def SbtChildProject(name: String): Project = (
    Project("sbt-child-" + name, file("sbt-child") / name)
    settings(snapDefaults:_*)
  )
  
  def SnapPlayProject(name: String): Project = (
    play.Project("snap-" + name, path = file(name)) 
    settings(snapDefaults:_*)
  )

  def SnapJavaProject(name: String): Project = (
    Project("snap-" + name, file(name))
    settings(snapDefaults:_*)
    settings(
        autoScalaLibrary := false
    )
  )
}

