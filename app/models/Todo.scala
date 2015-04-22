package models

/**
 * Created by pony on 15/04/20.
 */

import models.DAO._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.lifted.TableQuery

case class Todo(id: Option[Int] = None, description: Option[String] = None, done: Option[Boolean] = None)

object TodoInstances {
  implicit val todoReads: Reads[Todo] =
    ((JsPath \ "id").read[Option[Int]] and
      (JsPath \ "description").read[Option[String]] and
      (JsPath \ "done").read[Option[Boolean]])(Todo.apply _)

  implicit val todoWrites: Writes[Todo] = Json.writes[Todo]
}


class TodoTable(tag: Tag) extends Table[Todo](tag, "todos") {

  val id = column[Int]("id", O.AutoInc, O.PrimaryKey)
  val description = column[String]("description")
  val done = column[Boolean]("done")

  def * = (id.?, description.?, done.?) <>(Todo.tupled, Todo.unapply)
}

trait TodoDao {

  self: TableQuery[TodoTable] =>

  def findById(id: Int)(implicit s: Session): Option[Todo] =
    filter(_.id === id).firstOption

  def createTodo(t: Todo)(implicit s: Session) {
    Todos.insertOrUpdate(t)
  }

  def all()(implicit s: Session): List[Todo] = {
    Todos.list
  }

}