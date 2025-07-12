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
import model.EntryId
import rdts.base.Lattice
import scala.util.Failure
import scala.util.Success

object DexieDB {

  private val schemaVersion = 1.1

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

  def getEntry(id: EntryId): Future[Option[Entry]] =
    entriesTable
      .get(id.delegate)
      .toFuture
      .map(_.toOption.map(NativeConverter[Entry].fromNative(_)))

  /** Inserts an entry into the database and merges it with an existing entry if
    * it exists.
    *
    * @param entry
    *   The entry to be inserted or updated.
    */
  def upsertEntry(entry: Entry): Unit = {
    for {
      oldEntry <- getEntry(entry.id)
      newEntry = oldEntry match
        case Some(old) =>
          Lattice[Entry].merge(entry, old)
        case _ => entry
      result <- entriesTable.put(newEntry.toNative).toFuture
    } yield {
      result
    }
  }.onComplete {
    case Failure(exception) =>
      println(s"Failed to write entry to db: $exception")
    case Success(value) => ()
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
