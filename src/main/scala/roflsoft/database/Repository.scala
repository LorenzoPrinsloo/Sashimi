package roflsoft.database

import doobie.ConnectionIO
import doobie.quill.DoobieContext.Postgres
import doobie.quill.{ DoobieContext, DoobieContextBase }
import io.getquill.{ Escape, NamingStrategy }
import io.getquill.context.jdbc.JdbcContextBase
import io.getquill.context.sql.idiom.SqlIdiom
import io.roflsoft.encoding.DefaultEncoders
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import io.roflsoft.db.doobieCtx
import roflsoft.model.User

abstract class Repository[A] {
  val ctx = new DoobieContext.Postgres(Escape)
  import ctx._

  implicit val jodaTimeDecoder: Decoder[DateTime] =
    decoder((index, row) =>
      DateTime.parse(
        row.getObject(index).toString,
        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S")))

  implicit val jodaTimeEncoder: Encoder[DateTime] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => {
    val result = value.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"))
    row.setObject(index, result.replace('T', ' '), java.sql.Types.TIMESTAMP)
  })

}
