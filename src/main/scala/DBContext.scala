import io.getquill.jdbczio.Quill
import zio.ZLayer

import javax.sql.DataSource

object DBContext {
  val live: ZLayer[Any, Throwable, DataSource] = Quill.DataSource.fromPrefix("database")
}
