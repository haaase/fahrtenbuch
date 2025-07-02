package fahrtenbuch

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import fahrtenbuch.DexieDB.entriesObservable
import org.scalajs.dom
import rdts.base.Uid

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.*
import scala.util.Failure
import scala.util.Success

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
  val entryObserver =
    Observer[Entry](onNext = DexieDB.insertEntry(_))
  val entryPrinter =
    Observer[Entry](onNext = entry => println(entry))
//  entryEditBus --> entryObserver
//  entryEditBus --> entryPrinter
  entryEditBus.stream.tapEach(_ => println("lalilu"))
  println("test")

  val allEntriesVar = Var(Set.empty[Entry])
  entriesObservable.subscribe(entries =>
    entries.onComplete {
      case Failure(exception) => println("failed to get entries from db")
      case Success(value)     => allEntriesVar.set(value.toSet)
    }
  )
  val allEntries: Signal[List[Entry]] = allEntriesVar.signal.map(_.toList)
//  val allEntries = entryEditBus.stream.foldLeft(Map.empty[Uid, Entry]) {
//    case (acc, entry) =>
//      acc + (entry.id -> entry)
//  }
  entryEditBus.stream.addObserver(entryObserver)(using unsafeWindowOwner)

}
