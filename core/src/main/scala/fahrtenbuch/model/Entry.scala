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
    startKm: LastWriterWins[BigDecimal],
    endKm: LastWriterWins[BigDecimal],
    animal: LastWriterWins[String],
    paid: LastWriterWins[Boolean],
    driver: LastWriterWins[String],
    date: LastWriterWins[Double],
    gasPricePerKm: LastWriterWins[BigDecimal] = LastWriterWins.now(0.13),
    wearPricePerKm: LastWriterWins[BigDecimal] = LastWriterWins.now(0.05)
) derives NativeConverter:
  val distance = endKm.payload - startKm.payload

  // initial 13 cent pro km, 5 cent Abnutzung
  def costGas: BigDecimal = distance * gasPricePerKm.payload
  def costWear: BigDecimal = distance * wearPricePerKm.payload
  def costTotal: BigDecimal = costGas + costWear

object Entry:
  given Lattice[Entry] = Lattice.derived

  given NativeConverter[BigDecimal] with {
    extension (a: BigDecimal)
      override def toNative: js.Any =
        a.toString()
    override def fromNative(ps: ParseState): BigDecimal =
      BigDecimal(ps.json.asInstanceOf[String])
  }

  def apply(
      id: EntryId,
      startKm: LastWriterWins[BigDecimal],
      endKm: LastWriterWins[BigDecimal],
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
