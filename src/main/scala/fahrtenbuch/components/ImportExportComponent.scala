package fahrtenbuch.components

import com.raquo.laminar.api.L.*
import fahrtenbuch.DexieDB
import org.scalajs.dom
import org.scalajs.dom.{Blob, BlobPropertyBag, URL}
import scala.scalajs.js.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

class ImportExportComponent():
  private val canShare: Boolean = {
    val nav = js.Dynamic.global.navigator
    if (js.isUndefined(nav.share) || js.isUndefined(nav.canShare)) false
    else {
      val testFile = js.Dynamic.newInstance(js.Dynamic.global.File)(
        js.Array(""),
        "test.json",
        js.Dynamic.literal(`type` = "application/json")
      )
      nav
        .canShare(js.Dynamic.literal(files = js.Array(testFile)))
        .asInstanceOf[Boolean]
    }
  }

  private val shareError = Var(Option.empty[String])

  def render(): HtmlElement = {
    div(
      cls := "import-export",
      div(
        cls := "file",
        styleAttr := (if canShare then "" else "display:none"),
        onClick --> { _ =>
          DexieDB.dumpDB().onComplete {
            case scala.util.Success(result) =>
              val timestamp = new Date(Date.now()).toISOString()
              val filename =
                s"fahrtenbuch-export.json"
              val blob = new Blob(
                js.Array(result),
                new BlobPropertyBag { `type` = "application/json" }
              )
              val file = js.Dynamic.newInstance(js.Dynamic.global.File)(
                js.Array(blob),
                filename,
                js.Dynamic.literal(`type` = "application/json")
              )
              val navigator = js.Dynamic.global.navigator
              if (!js.isUndefined(navigator.share)) {
                navigator
                  .share(
                    js.Dynamic.literal(
                      files = js.Array(file),
                      title = "Fahrtenbuch Export"
                    )
                  )
              } else shareError.set(Some("Share not supported"))
            case scala.util.Failure(exception) =>
              shareError.set(Some(exception.getMessage))
          }
        },
        label(
          cls := "file-label",
          span(
            cls := "file-cta",
            span(
              cls := "file-icon",
              i(cls := "mdi mdi-share-variant")
            ),
            span(
              cls := "file-label",
              "Daten teilen"
            )
          )
        )
      ),
      p(
        cls := "help is-danger",
        child.text <-- shareError.signal.map(_.getOrElse("")),
        display <-- shareError.signal.map(e =>
          if e.isDefined then "" else "none"
        )
      ),
      div(
        cls := "file",
        onClick --> { _ =>
          DexieDB.dumpDB().onComplete {
            case scala.util.Success(result) =>
              val blob = new Blob(
                js.Array(result),
                new BlobPropertyBag {
                  `type` = "application/json"
                }
              )
              val objectUrl = URL.createObjectURL(blob)
              val anchor = dom.document
                .createElement("a")
                .asInstanceOf[dom.HTMLAnchorElement]
              anchor.href = objectUrl
              val timestamp = new Date(Date.now()).toISOString()
              anchor.download =
                s"fahrtenbuch-export-${dom.window.location.hash}-$timestamp.json"
              anchor.click()
              URL.revokeObjectURL(objectUrl)
            case scala.util.Failure(exception) =>
              println(s"Failed to export database: $exception")
          }
        },
        label(
          cls := "file-label",
          span(
            cls := "file-cta",
            span(
              cls := "file-icon",
              i(cls := "mdi mdi-download")
            ),
            span(
              cls := "file-label",
              "Daten exportieren"
            )
          )
        )
      ),
      div(
        cls := "file",
        label(
          cls := "file-label",
          input(
            cls := "file-input",
            tpe := "file",
            accept := ".json",
            styleAttr := "display:none",
            idAttr := "import-file-input",
            onChange --> { event =>
              val fileInput = event.target.asInstanceOf[dom.HTMLInputElement]
              Option(fileInput.files).filter(_.length > 0).foreach { files =>
                val file = files(0)
                val reader = new dom.FileReader()
                reader.onload = _ => {
                  val jsonString = reader.result.asInstanceOf[String]
                  DexieDB.loadDB(jsonString).onComplete {
                    case scala.util.Success(_) => println("Import erfolgreich")
                    case scala.util.Failure(exception) =>
                      println(s"Failed to import database: $exception")
                  }
                }
                reader.readAsText(file)
                fileInput.value = ""
              }
            }
          ),
          span(
            cls := "file-cta",
            span(
              cls := "file-icon",
              i(cls := "mdi mdi-upload")
            ),
            span(
              cls := "file-label",
              "Daten importieren"
            )
          )
        )
      )
    )
  }
