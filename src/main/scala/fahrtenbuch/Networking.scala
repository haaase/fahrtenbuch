package fahrtenbuch

import com.raquo.laminar.api.L.*
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
  val room: Room = joinRoom(MyConfig, "fahrtenbuch")
  val peerList: Var[List[(String, RTCPeerConnection)]] = Var(List.empty)
  val userId: Var[String] = Var(selfId)

  // listen for incoming messages
  def updatePeers(): Unit =
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
