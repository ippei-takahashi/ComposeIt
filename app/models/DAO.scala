package models

/**
 * Created by pony on 15/04/20.
 */
import scala.slick.lifted.TableQuery

object DAO {
  val Users = new TableQuery(tag => new UserTable(tag)) with UserDao
}