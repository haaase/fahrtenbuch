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
  val entryDbObserver =
    Observer[Entry](onNext = DexieDB.insertEntry(_))
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
  entryEditBus.stream.addObserver(entryDbObserver)(using unsafeWindowOwner)

}
