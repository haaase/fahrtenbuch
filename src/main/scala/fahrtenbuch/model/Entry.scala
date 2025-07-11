package fahrtenbuch.model

import rdts.base.Uid
import scala.scalajs.js.Date
import org.getshaka.nativeconverter.NativeConverter
import org.getshaka.nativeconverter.ParseState
import scala.scalajs.js
import rdts.datatypes.LastWriterWins

case class Entry(
    id: Uid,
    startKm: LastWriterWins[Double],
    endKm: LastWriterWins[Double],
    animal: LastWriterWins[String],
    paid: LastWriterWins[Boolean],
    driver: LastWriterWins[String],
    date: LastWriterWins[Date]
) derives NativeConverter:
  val distance = endKm.payload - startKm.payload

  // 13 cent pro km, 5 cent Abnutzung
  def costGas: Double = distance * 0.13
  def costWear: Double = distance * 0.05
  def costTotal: Double = costGas + costWear

object Entry:
  given NativeConverter[Uid] with {
    extension (a: Uid)
      override def toNative: js.Any =
        a.delegate
    override def fromNative(ps: ParseState): Uid =
      Uid.predefined(ps.json.asInstanceOf[String])
  }
