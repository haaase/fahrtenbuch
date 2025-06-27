package fahrtenbuch

import rdts.base.Uid
import scala.scalajs.js.Date
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.HTMLTableRowElement
import com.raquo.laminar.nodes.ReactiveHtmlElement
import fahrtenbuch.Main.editClickBus
import scala.annotation.threadUnsafe
import fahrtenbuch.Main.entryEditBus
import org.getshaka.nativeconverter.NativeConverter
import fahrtenbuch.Main.entryPrinter
import org.getshaka.nativeconverter.ParseState
import scala.scalajs.js

case class EntryComponent(
    entry: Entry,
    editMode: Boolean
):
  def render: ReactiveHtmlElement[HTMLTableRowElement] = {
    if editMode then
      val driverInput = input(cls := "input", value := entry.driver)
      val startKmInput =
        input(cls := "input", value := entry.startKm.toString())
      val endKmInput = input(cls := "input", value := entry.endKm.toString())
      val animalInput = input(cls := "input", value := entry.animal)
      val costWearInput =
        input(cls := "input", value := entry.costWear.toString())
      val costTotalInput =
        input(cls := "input", value := entry.costTotal.toString())
      val paidCheckbox = input(`type` := "checkbox", checked := entry.paid)
      tr(
//        td(input(cls := "input", value := entry.date.toDateString())),
        td(driverInput),
        td(startKmInput),
        td(endKmInput),
        td(animalInput),
        td(),
        td(),
        td(paidCheckbox),
        td(
          button(
            cls := "button is-success",
            onClick --> {
              editClickBus.emit(entry.id, false)
              entryEditBus.emit(
                entry.copy(
                  startKm = startKmInput.ref.value.toDouble,
                  endKm = endKmInput.ref.value.toDouble,
                  animal = animalInput.ref.value,
                  paid = paidCheckbox.ref.checked
                )
              )
            },
            entryEditBus.stream --> entryPrinter,
            span(
              cls := "icon edit",
              i(cls := "mdi mdi-18px mdi-check-bold")
            )
          )
        )
      )
    else
      tr(
        //       td(entry.date.toDateString()),
        td(entry.driver),
        td(entry.startKm),
        td(entry.endKm),
        td(entry.animal),
        td(s"${entry.costWear}€"),
        td(s"${entry.costTotal}€"),
        td(if entry.paid then "Ja" else "Nein"),
        td(
          button(
            cls := "button is-link",
            onClick --> editClickBus.emit(entry.id, true),
            span(cls := "icon edit", i(cls := "mdi mdi-18px mdi-pencil"))
          )
        )
      )
  }

case class Entry(
    id: Uid,
    startKm: Double,
    endKm: Double,
    animal: String,
    paid: Boolean,
    driver: String,
    date: Option[Date] = None
) derives NativeConverter:
  val distance = endKm - startKm

  // 13 cent pro km, 5 cent Abnutzung
  def costGas: Double = distance * 0.13
  def costWear: Double = distance * 0.05
  def costTotal: Double = costGas + costWear

object Entry:
  given NativeConverter[Uid] with {
    extension (a: Uid)
      override def toNative: js.Any =
        a.delegate
    override def fromNative(ps: ParseState): Uid =
      Uid.predefined(ps.json.asInstanceOf[String])
  }
