package fahrtenbuch.components

//import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.L.*

import com.raquo.laminar.api.features.unitArrows
import fahrtenbuch.Main.entryEditBus
import fahrtenbuch.model.{Entry, EntryId}
import rdts.datatypes.LastWriterWins
import scala.scalajs.js.Date
import scala.util.Try

class NewEntryInput(showNewEntryField: Var[Boolean]):
  val newEntryDriver = input(cls := "input")
  val newEntryStartKm = input(`type` := "number")
  val newEntryEndKm = input(`type` := "number")
  val newEntryAnimal = input(cls := "input")
  val newEntryPaid = input(`type` := "checkbox")

  // Validation signals
  val startKmValue = newEntryStartKm
    .events(onInput)
    .mapTo(newEntryStartKm.ref.value)
    .startWith("")
  val endKmValue = newEntryEndKm
    .events(onInput)
    .mapTo(newEntryEndKm.ref.value)
    .startWith("")

  val startKmValid =
    startKmValue.map(value => value.isEmpty || Try(value.toDouble).isSuccess)
  val endKmValid =
    endKmValue.map(value => value.isEmpty || Try(value.toDouble).isSuccess)

  val rangeValid = startKmValue.combineWithFn(endKmValue) { (start, end) =>
    if (start.isEmpty || end.isEmpty) true
    else {
      Try(start.toDouble).toOption.zip(Try(end.toDouble).toOption) match {
        case Some((startVal, endVal)) => endVal > startVal
        case None => true // Don't show range error if values are invalid
      }
    }
  }

  val startKmError = startKmValid.combineWithFn(rangeValid) { (valid, range) =>
    !valid || !range
  }

  val endKmError = endKmValid.combineWithFn(rangeValid) { (valid, range) =>
    !valid || !range
  }

  newEntryStartKm.amend(
    cls <-- startKmError.map(error =>
      if error then "input is-danger" else "input"
    )
  )

  newEntryEndKm.amend(
    cls <-- endKmError.map(error =>
      if error then "input is-danger" else "input"
    )
  )

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
            val id = EntryId.gen()
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
