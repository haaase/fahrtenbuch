package fahrtenbuch

import com.raquo.laminar.api.L.*
import fahrtenbuch.DexieDB.entriesObservable
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import model.Entry
import components.AppComponent
import fahrtenbuch.components.RoomIDComponent

@main
def Fahrtenbuch(): Unit =
  val hash = dom.window.location.hash

  lazy val appComponent = AppComponent(Main.allEntries, Trystero.onlineStatus)
  lazy val roomIDComponent = RoomIDComponent()

  if hash.nonEmpty then
    DexieDB.setRoomId(hash.stripPrefix("#"))
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      appComponent.render()
    )
  else
    DexieDB.getRoomId().onComplete {
      case Success(Some(storedId)) =>
        dom.window.location.hash = storedId
        dom.window.location.reload()
      case Success(None) =>
        println(s"no room id stored")
        render(
          dom.document.getElementById("app"),
          roomIDComponent.render()
        )
      case Failure(e) =>
        println(s"unknown error: $e")
    }

object Main {
  // track changes to entries
  val entryEditBus = new EventBus[Entry]
  val entryDbObserver =
    Observer[Entry](onNext = DexieDB.upsertEntry(_))
  entryEditBus.stream.tapEach(_ => println("lalilu"))

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

  // sync out changes
  entryEditBus.stream.addObserver(Sync.entrySyncOut)(using unsafeWindowOwner)

  val allEntries: Signal[Set[Entry]] =
    allEntriesVar.signal

}
