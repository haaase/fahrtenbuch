package fahrtenbuch

import scala.scalajs.js.Date

case class Entry(startKm: Double, endKm: Double, date: Date, animal: String, paid: Boolean, driver: String):
  val distance = endKm - startKm

  // 13 cent pro km, 5 cent Abnutzung
  def costGas: Double = distance * 0.13
  def costWear: Double = distance * 0.05
  def costTotal: Double = costGas + costWear
