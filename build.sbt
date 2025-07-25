import org.scalajs.linker.interface.ModuleSplitStyle

lazy val fahrtenbuch = project
  .in(file("."))
  .enablePlugins(
    ScalaJSPlugin,
    ScalablyTypedConverterExternalNpmPlugin
  )
  .settings(
    scalaVersion := "3.7.1",
    scalacOptions += "-Xfatal-warnings",
    scalacOptions += "-Wunused:imports",

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    // scalably typed config
    // Ignore several Trystero dependencies in ScalablyTyped to avoid `stImport` errors
    stIgnore := List(
      "libp2p",
      "firebase",
      "@supabase/supabase-js",
      "@mdi/font",
      "bulma"
    ),
    externalNpm := baseDirectory.value,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "livechart" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("fahrtenbuch"))
        )
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "17.2.1",
    libraryDependencies += "de.tu-darmstadt.stg" %%% "rdts" % "0.37.0",
    libraryDependencies += "org.getshaka" %%% "native-converter" % "0.9.0"
  )
