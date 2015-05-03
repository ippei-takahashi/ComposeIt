package controllers

import models.DAO._
import models.Todo
import models.TodoInstances._
import play.api.Play.current
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {
  def index = Action {
    Ok(views.html.index())
  }

  def fetchTodo = Action {
    DB.withSession { implicit session =>
      Ok(Json.toJson(Todos.all()))
    }
  }

  def saveTodo = Action { req =>
    (for {
      json <- req.body.asJson
      todo <- Json.fromJson[Todo](json).asOpt
    } yield todo) match {
      case Some(todo) =>
        DB.withSession { implicit session =>
          Todos.createTodo(todo)
          Ok("")
        }
      case _ => NotFound
    }
  }
}
