package fahrtenbuch

import scala.scalajs.js.Date
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.HTMLTableRowElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

type Id = String

case class EntryComponent(
    entry: Entry,
    editMode: Boolean
):
  def render: ReactiveHtmlElement[HTMLTableRowElement] = {
    if editMode then
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
    else
      tr(
        td(entry.date.toDateString()),
        td(entry.driver),
        td(entry.startKm),
        td(entry.endKm),
        td(entry.animal),
        td(entry.costWear),
        td(entry.costTotal),
        td(entry.paid),
        td(
          button(
            cls := "button is-link",
            span(cls := "icon edit", i(cls := "mdi mdi-18px mdi-pencil"))
          )
        )
      )
  }

case class Entry(
    id: Id,
    startKm: Double,
    endKm: Double,
    date: Date,
    animal: String,
    paid: Boolean,
    driver: String
):
  val distance = endKm - startKm

  // 13 cent pro km, 5 cent Abnutzung
  def costGas: Double = distance * 0.13
  def costWear: Double = distance * 0.05
  def costTotal: Double = costGas + costWear
