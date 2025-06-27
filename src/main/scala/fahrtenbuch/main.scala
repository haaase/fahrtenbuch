package fahrtenbuch

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import scala.scalajs.js.Date
import rdts.base.Uid

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

  // tracks whenever a user clicks on an edit button
  val editClickBus = new EventBus[(Uid, Boolean)]
  val editStateSignal: Signal[Map[Uid, Boolean]] =
    editClickBus.stream.foldLeft(Map.empty[Uid, Boolean]) {
      case (acc, (id, value)) =>
        acc + (id -> value)
    }

  // track changes to entries
  val entryEditBus = new EventBus[Entry]
  val entryObserver =
    Observer[Entry](onNext = DexieDB.insertEntry(_))
  val entryPrinter =
    Observer[Entry](onNext = entry => println(entry))
//  entryEditBus --> entryObserver
//  entryEditBus --> entryPrinter
  entryEditBus.stream.tapEach(_ => println("lalilu"))
  println("test")

  val allEntries = entryEditBus.stream.foldLeft(Map.empty[Uid, Entry]) {
    case (acc, entry) =>
      acc + (entry.id -> entry)
  }
  entryEditBus.stream.addObserver(entryObserver)(using unsafeWindowOwner)

  val entryComponents: Signal[List[EntryComponent]] =
    allEntries
      .combineWith(editStateSignal)
      .map { case (entries, editState) =>
        entries.values.toList
          .sortBy(_.id)
          .map(entry =>
            EntryComponent(entry, editState.getOrElse(entry.id, false))
          )
      }

  val showNewEntryField = Var(false)

  val newEntryInput =
    val newEntryDriver = input(cls := "input")
    val newEntryStartKm = input(cls := "input")
    val newEntryEndKm = input(cls := "input")
    val newEntryAnimal = input(cls := "input")
    val newEntryPaid = input(`type` := "checkbox")
    tr(
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
            val driver = newEntryDriver.ref.value
            val startKm = newEntryStartKm.ref.value.toDouble
            val endKm = newEntryEndKm.ref.value.toDouble
            val animal = newEntryAnimal.ref.value
            val paid = newEntryPaid.ref.checked
            entryEditBus.emit(
              Entry(id, startKm, endKm, animal, paid, driver)
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

  def appElement(): HtmlElement =
    div(
      cls := "app content",
      h1("Fahrtenbuch"),
      table(
        cls := "table",
        thead(
          tr(
//            th("Date"),
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
          children <-- entryComponents.map(_.map(_.render)),
          child(newEntryInput) <-- showNewEntryField
        )
      ),
      button(
        cls := "button is-primary",
        onClick --> { _ =>
          showNewEntryField.set(true)
        },
        "Eintrag hinzufügen"
      )
    )
}
