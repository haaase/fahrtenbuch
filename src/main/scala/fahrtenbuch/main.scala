package fahrtenbuch

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js.Date

// import javascriptLogo from "/javascript.svg"
//@js.native @JSImport("/javascript.svg", JSImport.Default)
//val javascriptLogo: String = js.native

@main
def Fahrtenbuch(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
  println("Hello, Worlds!")

object Main {

  val entries = List(
    Entry(100.0, 200.0, new Date(), "🐷", true, "Gesine"),
    Entry(200.0, 300.0, new Date(), "Dog", false, "Bob")
  )

  def appElement(): HtmlElement =
    div(
      cls := "app content",
      h1("Fahrtenbuch"),
      table(
        cls := "table",
        thead(
          tr(
            th("Date"),
            th("Fahrer*in"),
            th("Start Km"),
            th("Ende Km"),
            th("Tier"),
            th("Abnutzung"),
            th("Gesamtkosten"),
            th("Bezahlt")
          )
        ),
        tbody(
          entries.map(entry =>
            tr(
              td(entry.date.toDateString()),
              td(entry.driver),
              td(entry.startKm),
              td(entry.endKm),
              td(entry.animal),
              td(entry.costWear),
              td(entry.costTotal),
              td(entry.paid),
              td(span(cls := "icon", i(cls := "mdi mdi-pencil-box")))
            )
          )
        )
      ),
      button(cls := "button is-primary", "Eintrag hinzufügen")
    )
}
