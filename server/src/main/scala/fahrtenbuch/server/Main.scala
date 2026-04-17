package fahrtenbuch.server

import cats.effect.IOApp
import cats.effect.IO
import Server.server

object Main extends IOApp.Simple {
  override def run: IO[Unit] = server[IO]
}
