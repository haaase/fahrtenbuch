package fahrtenbuch.components

import com.raquo.laminar.api.L.*
import fahrtenbuch.DexieDB

import scala.concurrent.ExecutionContext.Implicits.global

class ImportExportComponent():
  def render(): HtmlElement = {
    button(
      cls := "button",
      onClick --> { _ =>
        DexieDB.dumpDB().onComplete {
          case scala.util.Success(result) => println(result)
          case scala.util.Failure(exception) =>
            println(s"Failed to export database: $exception")
        }
      },
      "Daten exportieren"
    )
  }
