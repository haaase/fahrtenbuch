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

  val entries = Var(
    List(
      Entry("0", 100.0, 200.0, new Date(), "🐷", true, "Gesine"),
      Entry("1", 200.0, 300.0, new Date(), "Dog", false, "Bob")
    )
  )
  val entriesSignal = entries.signal

  val editState = Var(Map.empty[Id, Boolean])
  val editStateSignal = editState.signal

  val entryComponents: Signal[List[EntryComponent]] =
    entriesSignal
      .combineWith(editStateSignal)
      .map { case (entries, editState) =>
        entries.map(entry =>
          EntryComponent(entry, editState.getOrElse(entry.id, false))
        )
      }

  val editClickBus = new EventBus[(Id, Boolean)]

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
            th("Bezahlt"),
            th()
          )
        ),
        tbody(
          children <-- entryComponents.map(_.map(_.render))
        ),
        tr(
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(input(cls := "input")),
          td(
            button(
              cls := "button is-success",
              span(
                cls := "icon edit",
                i(cls := "mdi mdi-18px mdi-check-bold")
              )
            )
          )
        )
      ),
      button(cls := "button is-primary", "Eintrag hinzufügen")
    )
}
