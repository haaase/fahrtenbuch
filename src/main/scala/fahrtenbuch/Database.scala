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
import rdts.datatypes.LastWriterWins
import scala.scalajs.js.Date
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

  def insertEntry(entry: Entry): Unit = {
    val e: Future[Option[Entry]] = getEntry(entry.id)
    e.flatMap {
      case Some(oldEntry) =>
        println(s"found old: $oldEntry")
        println(s"found new: $entry")
        println(oldEntry.id == entry.id)
        val newEntry = Lattice[Entry].merge(entry, entry)
        val newEntry2 = Lattice[Entry].merge(oldEntry, oldEntry)
        println(oldEntry.id)
        println(entry.id)
        val test =
          Lattice[Entry].merge(
            Entry(
              EntryId("1"),
              LastWriterWins.now(0),
              LastWriterWins.now(2),
              LastWriterWins.now(""),
              LastWriterWins.now(false),
              LastWriterWins.now("Dirk"),
              LastWriterWins.now(new Date())
            ),
            Entry(
              EntryId("1"),
              LastWriterWins.now(0),
              LastWriterWins.now(2),
              LastWriterWins.now(""),
              LastWriterWins.now(false),
              LastWriterWins.now("Dirk"),
              LastWriterWins.now(new Date())
            )
          )
        val newEntry3 =
          Lattice[Entry].merge(oldEntry, entry.copy(id = oldEntry.id))
        println("yolo")
        Future.unit
      // entriesTable.put(newEntry.toNative).toFuture
      case _ =>
        entriesTable.put(entry.toNative).toFuture
    }.onComplete {
      case Failure(exception) => println(s"failed with $exception")
      case Success(value)     => ()
    }
//        .toFuture
//        .map(e =>
//          if e.isUndefined then entriesTable.put(entry.toNative).toFuture
//          else
//            val dbEntry = NativeConverter[Entry].fromNative(e)
//            Lattice[Entry].merge(entry, dbEntry)
//        )
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
