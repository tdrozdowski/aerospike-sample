package models

import eu.unicredit.reactive_aerospike.data.{AerospikeBinProto, AerospikeKey, AerospikeRecord}
import eu.unicredit.reactive_aerospike.model.Dao
import eu.unicredit.reactive_aerospike.model.experimental._
import eu.unicredit.reactive_aerospike.data.AerospikeValue._
import play.api.libs.json.Json

/**
 * User model & DAO
 *
 * Created by terry on 3/15/15.
 */
case class User(id : AerospikeKey[String], firstName : String, lastName : String, email : String) extends EqualUser

object User {
  implicit val userFormats = Json.format[User]
}

trait EqualUser {
  self: User =>
  override def equals(p2: Any) = {
    p2 match {
      case user: User =>
        user.firstName == self.firstName && user.lastName == self.lastName && user.email == self.email
      case _ => false
    }
  }
}

object UserDao extends Dao[String, User] {

  val namespace = "test"
  val setName = "users"

  def getKeyDigest(obj: User)= Dao.macroKeyDigest[User](obj)

  val objWrite : Seq[AerospikeBinProto[User, _]]  = Dao.macroObjWrite[User]

  val objRead : (AerospikeKey[String], AerospikeRecord) => User = Dao.macroObjRead[User][String]

}