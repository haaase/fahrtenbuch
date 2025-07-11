package fahrtenbuch

import com.raquo.laminar.api.L.*
import fahrtenbuch.DexieDB.entriesObservable
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import model.Entry
import components.AppComponent

@main
def Fahrtenbuch(): Unit =
  val appComponent = AppComponent(Main.allEntries)

  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    AppComponent(Main.allEntries).render()
  )

object Main {

  // track changes to entries
  val entryEditBus = new EventBus[Entry]
  val entryDbObserver =
    Observer[Entry](onNext = DexieDB.insertEntry(_))
  entryEditBus.stream.tapEach(_ => println("lalilu"))
  println("test")

  val allEntriesVar = Var(Set.empty[Entry])

  // update entries whenever db updates
  entriesObservable.subscribe(entries =>
    entries.onComplete {
      case Failure(exception) => println("failed to get entries from db")
      case Success(value)     => allEntriesVar.set(value.toSet)
    }
  )

  // update db when edit events happen
  entryEditBus.stream.addObserver(entryDbObserver)(using unsafeWindowOwner)

  val allEntries: Signal[Set[Entry]] =
    allEntriesVar.signal

}
