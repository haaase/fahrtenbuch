package fahrtenbuch.components
import fahrtenbuch.model.Entry
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLTableRowElement
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import rdts.base.Uid

class EntryComponent(
    entry: Entry,
    editMode: Boolean,
    editClickBus: EventBus[(Uid, Boolean)],
    entryEditBus: EventBus[Entry]
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
