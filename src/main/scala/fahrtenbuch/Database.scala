package fahrtenbuch

import org.getshaka.nativeconverter.NativeConverter
import org.scalablytyped.runtime.StringDictionary
import typings.dexie.mod.Dexie
import typings.dexie.mod.Observable
import typings.dexie.mod.Table
import typings.dexie.mod.liveQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

import model.Entry

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
