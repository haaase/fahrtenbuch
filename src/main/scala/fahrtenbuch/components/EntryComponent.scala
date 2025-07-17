package fahrtenbuch.components
import fahrtenbuch.model.{Entry, EntryId}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLTableRowElement
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import scala.util.Try
import scala.scalajs.js.Date

class EntryComponent(
    entry: Entry,
    editMode: Boolean,
    editClickBus: EventBus[(EntryId, Boolean)],
    entryEditBus: EventBus[Entry]
):
  def render: ReactiveHtmlElement[HTMLTableRowElement] = {
    if editMode then
      val driverInput = input(cls := "input", value := entry.driver.payload)

      val startKmInput =
        input(
          `type` := "number",
          value := entry.startKm.payload.toString()
        )
      val endKmInput =
        input(
          `type` := "number",
          value := entry.endKm.payload.toString()
        )

      // Validation signals
      val startKmValue = startKmInput
        .events(onInput)
        .mapTo(startKmInput.ref.value)
        .startWith(entry.startKm.payload.toString())
      val endKmValue = endKmInput
        .events(onInput)
        .mapTo(endKmInput.ref.value)
        .startWith(entry.endKm.payload.toString())

      val startKmValid =
        startKmValue.map(value => Try(value.toDouble).isSuccess)
      val endKmValid = endKmValue.map(value => Try(value.toDouble).isSuccess)

      val rangeValid = startKmValue.combineWithFn(endKmValue) { (start, end) =>
        Try(start.toDouble).toOption.zip(Try(end.toDouble).toOption) match {
          case Some((startVal, endVal)) => endVal > startVal
          case None => true // Don't show range error if values are invalid
        }
      }

      val startKmError = startKmValid.combineWithFn(rangeValid) {
        (valid, range) =>
          !valid || !range
      }

      val endKmError = endKmValid.combineWithFn(rangeValid) { (valid, range) =>
        !valid || !range
      }

      startKmInput.amend(
        cls <-- startKmError.map(error =>
          if error then "input is-danger" else "input"
        )
      )

      endKmInput.amend(
        cls <-- endKmError.map(error =>
          if error then "input is-danger" else "input"
        )
      )

      val animalInput = input(cls := "input", value := entry.animal.payload)
      val costWearInput =
        input(cls := "input", value := entry.costWear.toString())
      val costTotalInput =
        input(cls := "input", value := entry.costTotal.toString())
      val paidCheckbox =
        input(`type` := "checkbox", checked := entry.paid.payload)
      tr(
        td(),
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
              val newDriver =
                if driverInput.ref.value != entry.driver.payload then
                  entry.driver.write(driverInput.ref.value)
                else entry.driver
              val newStartKm =
                if startKmInput.ref.value != entry.startKm.payload.toString then
                  entry.startKm.write(BigDecimal(startKmInput.ref.value))
                else entry.startKm
              val newEndKm =
                if endKmInput.ref.value != entry.endKm.payload.toString then
                  entry.endKm.write(BigDecimal(endKmInput.ref.value))
                else entry.endKm
              val newAnimal =
                if animalInput.ref.value != entry.animal.payload then
                  entry.animal.write(animalInput.ref.value)
                else entry.animal
              val newPaid =
                if paidCheckbox.ref.checked != entry.paid.payload then
                  entry.paid.write(paidCheckbox.ref.checked)
                else entry.paid
              entryEditBus.emit(
                entry.copy(
                  driver = newDriver,
                  startKm = newStartKm,
                  endKm = newEndKm,
                  animal = newAnimal,
                  paid = newPaid
                )
              )
              editClickBus.emit(entry.id, false)
            },
            span(
              cls := "icon edit",
              i(cls := "mdi mdi-18px mdi-check-bold")
            )
          )
        )
      )
    else
      tr(
        td(new Date(entry.date.payload).toDateString()),
        td(entry.driver.payload),
        td(
          entry.startKm.payload
            .setScale(0, BigDecimal.RoundingMode.HALF_UP)
            .toString()
        ),
        td(
          entry.endKm.payload
            .setScale(0, BigDecimal.RoundingMode.HALF_UP)
            .toString()
        ),
        td(entry.animal.payload),
        td(s"${entry.costWear.setScale(2, BigDecimal.RoundingMode.HALF_UP)}€"),
        td(s"${entry.costTotal.setScale(2, BigDecimal.RoundingMode.HALF_UP)}€"),
        td(if entry.paid.payload then "Ja" else "Nein"),
        td(
          button(
            cls := "button is-link",
            onClick --> editClickBus.emit(entry.id, true),
            span(cls := "icon edit", i(cls := "mdi mdi-18px mdi-pencil"))
          )
        )
      )
  }
