package fahrtenbuch

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import org.scalajs.dom.RTCConfiguration
import org.scalajs.dom.RTCIceServer
import org.scalajs.dom.RTCPeerConnection
import typings.trystero.mod.BaseRoomConfig
import typings.trystero.mod.RelayConfig
import typings.trystero.mod.Room
import typings.trystero.mod.TurnConfig
import typings.trystero.mod.joinRoom
import typings.trystero.mod.selfId

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*
import typings.trystero.mod.ActionProgress
import typings.trystero.mod.ActionSender
import typings.trystero.mod.ActionReceiver
import model.Entry
import org.getshaka.nativeconverter.NativeConverter
import fahrtenbuch.Trystero.updatePeers

object Trystero:
  private val eturn = new RTCIceServer:
    urls = js.Array(
      "stun:relay1.expressturn.com:443",
      "turn:relay1.expressturn.com:3478",
      "turn:relay1.expressturn.com:443"
    )
    username = "efMS8M021S1G8NJ8J7"
    credential = "qrBXTlhKtCJDykOK"

  private val tturn = new RTCIceServer:
    urls = "stun:stun.t-online.de:3478"

  private val rtcConf = new RTCConfiguration:
    iceServers = js.Array(eturn, tturn)

  private object MyConfig extends RelayConfig, BaseRoomConfig, TurnConfig {
    var appId = "fahrtenbuch_149520"
    rtcConfig = rtcConf
  }

  // Public API
  val roomId = dom.window.location.hash
  val room: Room = joinRoom(MyConfig, roomId)
  println(s"joining room $roomId")
  val userId: Var[String] = Var(selfId)

  // track online peers
  val peerList: Var[List[(String, RTCPeerConnection)]] = Var(List.empty)
  def updatePeers(): Unit =
    println(s"List of peers: ${room.getPeers().toList}")
    peerList.set(room.getPeers().toList)
  println(s"my peer ID is $selfId")
  room.onPeerJoin(peerId =>
    println(s"$peerId joined")
    updatePeers()
  )
  room.onPeerLeave(peerId =>
    println(s"$peerId left")
    updatePeers()
  )
  val onlineStatus: Signal[Boolean] = peerList.signal.map(_.nonEmpty)

object Actions:
  // setup actions
  private val entryAction: js.Tuple3[ActionSender[js.Any], ActionReceiver[
    js.Any
  ], ActionProgress] = Trystero.room.makeAction[js.Any]("entry")
  private val trysteroReceiveEntry: ActionReceiver[js.Any] = entryAction._2

  def sendEntry(entry: Entry): Unit =
    entryAction._1(entry.toNative)

  def sendEntry(entry: Entry, targetPeers: List[String]): Unit =
    if targetPeers.isEmpty then sendEntry(entry)
    else
      entryAction._1(data = entry.toNative, targetPeers = targetPeers.toJSArray)

  def receiveEntry(callback: Entry => Unit): Unit =
    entryAction._2((data: js.Any, peerId: String, metaData) =>
      val incoming = NativeConverter[Entry].fromNative(data)
      callback(incoming)
    )

  // update peers when receiving entries
  receiveEntry(_ => updatePeers())
