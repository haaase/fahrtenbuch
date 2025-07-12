package fahrtenbuch.model

import rdts.base.Uid
import scala.scalajs.js.Date
import org.getshaka.nativeconverter.NativeConverter
import org.getshaka.nativeconverter.ParseState
import scala.scalajs.js
import rdts.datatypes.LastWriterWins
import rdts.base.Lattice

opaque type EntryId = Uid
object EntryId:
  def gen(): EntryId = Uid.gen()
  def apply(id: String): EntryId = Uid.predefined(id)
  extension (id: EntryId) def delegate: String = id.delegate

  given NativeConverter[EntryId] with {
    extension (a: EntryId)
      override def toNative: js.Any =
        a.delegate
    override def fromNative(ps: ParseState): Uid =
      Uid.predefined(ps.json.asInstanceOf[String])
  }

  given Lattice[EntryId] = Lattice.assertEquals

case class Entry(
    id: EntryId,
    startKm: LastWriterWins[Double],
    endKm: LastWriterWins[Double],
    animal: LastWriterWins[String],
    paid: LastWriterWins[Boolean],
    driver: LastWriterWins[String],
    date: LastWriterWins[Double]
) derives NativeConverter:
  val distance = endKm.payload - startKm.payload

  // 13 cent pro km, 5 cent Abnutzung
  def costGas: Double = distance * 0.13
  def costWear: Double = distance * 0.05
  def costTotal: Double = costGas + costWear

object Entry:
  given Lattice[Entry] = Lattice.derived

  def apply(
      id: EntryId,
      startKm: LastWriterWins[Double],
      endKm: LastWriterWins[Double],
      animal: LastWriterWins[String],
      paid: LastWriterWins[Boolean],
      driver: LastWriterWins[String]
  ): Entry =
    Entry(
      id,
      startKm,
      endKm,
      animal,
      paid,
      driver,
      LastWriterWins.now(Date.now())
    )
