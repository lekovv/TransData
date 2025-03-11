package endpoints

import zio.http._

object Health {

  val routes: Routes[Any, Nothing] = {
    Routes(
      Method.GET / "health" -> handler(Response.ok)
    )
  }
}
