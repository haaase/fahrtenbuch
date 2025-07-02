package fahrtenbuch

import org.scalablytyped.runtime.StringDictionary
import typings.dexie.mod.Dexie
import scala.concurrent.Future
import typings.dexie.mod.{Table, liveQuery}
import scala.scalajs.js
import org.getshaka.nativeconverter.NativeConverter
import scala.concurrent.ExecutionContext.Implicits.global
import typings.dexie.mod.Observable
import com.raquo.airstream.core.Signal
import com.raquo.airstream.core.EventStream

object DexieDB {

  private val schemaVersion = 1.0

  private val dexieDB: Dexie = new Dexie.^("fahrtenbuch")
  dexieDB
    .version(schemaVersion)
    .stores(
      StringDictionary(
        ("entries", "id")
      )
    )

  private val entriesTable: Table[js.Any, String, js.Any] =
    dexieDB.table("entries")
  val entriesObservable: Observable[Future[Seq[Entry]]] =
    liveQuery(() => getAllEntries())

  def insertEntry(entry: Entry): Future[Any] = {
    println(s"inserting Entry $entry")
    entriesTable.put(entry.toNative).toFuture
  }

  def getAllEntries(): Future[Seq[Entry]] = {
    entriesTable.toArray().toFuture.map { entriesJsArray =>
      entriesJsArray
        .map(
          NativeConverter[Entry].fromNative(_)
        )
        .toSeq
    }
  }
}
