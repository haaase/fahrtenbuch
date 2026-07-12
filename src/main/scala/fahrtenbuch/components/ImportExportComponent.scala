package fahrtenbuch.components

import com.raquo.laminar.api.L.*
import fahrtenbuch.DexieDB
import org.scalajs.dom
import org.scalajs.dom.{Blob, BlobPropertyBag, URL}
import scala.scalajs.js.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

class ImportExportComponent():
  def render(): HtmlElement = {
    div(
      cls := "import-export",
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
