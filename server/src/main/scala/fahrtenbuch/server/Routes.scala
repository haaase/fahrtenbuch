package fahrtenbuch.server

import fs2.io.file.Files
import cats.MonadThrow
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, HttpRoutes}

class Routes[F[_]: {Files, MonadThrow}] extends Http4sDsl[F] {

  def service: HttpApp[F] = {

    HttpRoutes.of[F] { case request @ GET -> Root / "api" / "pull" =>
      Ok("pull")
    }

  }.orNotFound

}
