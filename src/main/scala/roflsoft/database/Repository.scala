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
import io.roflsoft.db.{ PostgresModel, doobieCtx }
import roflsoft.model.{ Role, User }
import doobie._
import fs2._

abstract class Repository[A <: PostgresModel] {
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

  def createQuote(a: A)(implicit insertMeta: InsertMeta[A], schemaMeta: SchemaMeta[A]): Quoted[Insert[A]] = quote {
    query[A].insert(lift(a))
  }

  def updateQuote(a: A)(implicit insertMeta: UpdateMeta[A], schemaMeta: SchemaMeta[A]): Quoted[Update[A]] = {
    quote {
      query[A].update(a)
    }
  }

  def findByIdQuote(id: Long)(implicit schemaMeta: SchemaMeta[A]): Quoted[EntityQuery[A]] = {
    quote {
      query[A].filter(_.id == lift(id))
    }
  }

  def deleteByIdQuote(id: Long)(implicit schemaMeta: SchemaMeta[A]): Quoted[Delete[A]] = {
    quote {
      query[A].filter(_.id == lift(id)).delete
    }
  }

  def deleteQuote(a: A)(implicit schemaMeta: SchemaMeta[A]): Quoted[Delete[A]] = {
    deleteByIdQuote(a.id)
  }

}
