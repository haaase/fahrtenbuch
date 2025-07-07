package fahrtenbuch.components

import com.raquo.laminar.api.L.*
import fahrtenbuch.model.Entry
import fahrtenbuch.Main.entryEditBus
import rdts.base.Uid

class AppComponent(allEntries: Signal[List[Entry]]):
  // tracks whenever a user clicks on an edit button
  val editClickBus = new EventBus[(Uid, Boolean)]

  // tracks which entries are currently being edited
  val editStateSignal: Signal[Map[Uid, Boolean]] =
    editClickBus.stream.foldLeft(Map.empty[Uid, Boolean]) {
      case (acc, (id, value)) =>
        acc + (id -> value)
    }

  val entryComponents: Signal[List[EntryComponent]] =
    allEntries
      .combineWith(editStateSignal)
      .map { case (entries, editState) =>
        entries.toList
          .sortBy(_.id)
          .map(entry =>
            EntryComponent(
              entry,
              editState.getOrElse(entry.id, false),
              editClickBus,
              entryEditBus
            )
          )
      }

  val showNewEntryField = Var(false)

  def render(): HtmlElement =
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
          child(NewEntryInput(showNewEntryField).render) <-- showNewEntryField
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
end AppComponent
