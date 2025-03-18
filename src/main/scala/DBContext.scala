import io.getquill.jdbczio.Quill
import zio.TaskLayer

import javax.sql.DataSource

object DBContext {
  val live: TaskLayer[DataSource] = Quill.DataSource.fromPrefix("database")
}
