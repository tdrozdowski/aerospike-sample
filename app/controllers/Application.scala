package controllers

import eu.unicredit.reactive_aerospike.client.AerospikeClient
import eu.unicredit.reactive_aerospike.data.AerospikeKey
import eu.unicredit.reactive_aerospike.future.ScalaFactory
import models.User
import models.User.userFormats
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  implicit val client = new AerospikeClient("localhost", 3000)(ScalaFactory)

  def index = Action {
    Ok("")
  }

  def createUser = Action.async(parse.json[User]) {
    request =>
      User.create(request.body).map {
        key =>
          Logger.debug(s"Wrote ${request.body} to db, key : $key")
          Ok(Json.obj("key" -> key.userKey.fold("no key")(_.toString), "digest" -> new String(key.digest)))
      }
  }

  def getUser(email: String) = Action.async {
    request => {
      User.read(AerospikeKey(User.namespace, User.setName, email)).map {
        user =>
          Ok(Json.toJson(user))
      } recover {
        case e =>
          e.printStackTrace()
          InternalServerError(s"Error searching for User with email : $email => ${e.getMessage}")
      }
    }
  }

}