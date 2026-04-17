package fahrtenbuch.server

import cats.effect.Async
import fs2.io.file.Files
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import cats.syntax.all.*

object Server {

  def server[F[_]: {Async, Files, Network}]: F[Unit] = {
    val host = host"0.0.0.0"
    val port = port"8080"

    EmberServerBuilder
      .default[F]
      .withHost(host)
      .withPort(port)
      .withHttpApp(new Routes().service)
      .build
      .useForever
      .void
  }

}