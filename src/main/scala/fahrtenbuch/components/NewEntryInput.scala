package fahrtenbuch.components

//import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.L.*

import com.raquo.laminar.api.features.unitArrows
import fahrtenbuch.Main.entryEditBus
import fahrtenbuch.model.Entry
import rdts.base.Uid
import rdts.datatypes.LastWriterWins
import scala.scalajs.js.Date

class NewEntryInput(showNewEntryField: Var[Boolean]):
  val newEntryDriver = input(cls := "input")
  val newEntryStartKm = input(cls := "input")
  val newEntryEndKm = input(cls := "input")
  val newEntryAnimal = input(cls := "input")
  val newEntryPaid = input(`type` := "checkbox")

  def render =
    tr(
      td(),
      td(newEntryDriver),
      td(newEntryStartKm),
      td(newEntryEndKm),
      td(newEntryAnimal),
      td(),
      td(),
      td(newEntryPaid),
      td(
        button(
          cls := "button is-success",
          onClick --> {
            val id = Uid.gen()
            val driver = LastWriterWins.now(newEntryDriver.ref.value)
            val startKm = LastWriterWins.now(newEntryStartKm.ref.value.toDouble)
            val endKm = LastWriterWins.now(newEntryEndKm.ref.value.toDouble)
            val animal = LastWriterWins.now(newEntryAnimal.ref.value)
            val paid = LastWriterWins.now(newEntryPaid.ref.checked)
            val date = LastWriterWins.now(new Date(Date.now()))
            entryEditBus.emit(
              Entry(id, startKm, endKm, animal, paid, driver, date)
            )
            showNewEntryField.set(false)
            newEntryDriver.ref.value = ""
            newEntryStartKm.ref.value = ""
            newEntryEndKm.ref.value = ""
            newEntryAnimal.ref.value = ""
            newEntryPaid.ref.checked = false
          },
          span(
            cls := "icon edit",
            i(cls := "mdi mdi-18px mdi-check-bold")
          )
        )
      )
    )
