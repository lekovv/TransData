package scheduler

import config.ConfigApp
import exception.AppError
import zio.{Schedule, URLayer, ZIO, ZLayer, durationInt}

case class SchedulerLive() {

  def repeat24hours[A](action: () => ZIO[ConfigApp, AppError, A]): ZIO[ConfigApp, AppError, Long] = {
    (for {
      _ <- action()
      _ <- ZIO.logInfo("new data has been received")
    } yield ())
      .repeat(Schedule.spaced(24.hours))
  }
}

object SchedulerLive {
  val layer: URLayer[Any, SchedulerLive] = ZLayer.fromFunction(SchedulerLive.apply _)
}
