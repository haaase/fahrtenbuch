package fahrtenbuch

import rdts.base.Uid
import scala.scalajs.js.Date
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.HTMLTableRowElement
import com.raquo.laminar.nodes.ReactiveHtmlElement
import scala.annotation.threadUnsafe
import fahrtenbuch.Main.entryEditBus
import org.getshaka.nativeconverter.NativeConverter
import fahrtenbuch.Main.entryPrinter
import org.getshaka.nativeconverter.ParseState
import scala.scalajs.js

case class Entry(
    id: Uid,
    startKm: Double,
    endKm: Double,
    animal: String,
    paid: Boolean,
    driver: String,
    date: Option[Date] = None
) derives NativeConverter:
  val distance = endKm - startKm

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
