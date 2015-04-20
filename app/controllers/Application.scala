package controllers

import play.api.mvc._
import scalaz._
import Scalaz._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index(NonEmptyList(NonEmptyList(1, 2, 3), NonEmptyList(4, 5, 6)).join.toString))
  }

}