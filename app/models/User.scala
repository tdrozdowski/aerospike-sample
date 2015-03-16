package models

import eu.unicredit.reactive_aerospike.data.{AerospikeBinProto, AerospikeKey, AerospikeRecord}
import eu.unicredit.reactive_aerospike.model.Dao
import play.api.libs.json.Json

/**
 * User model & DAO
 *
 * Created by terry on 3/15/15.
 */
case class User(id : Option[String] = None, firstName : String, lastName : String, email : String)

object User extends Dao[String, User] {

  implicit val userFormats = Json.format[User]

  val namespace = "test"
  val setName = "users"

  def getKeyDigest(obj: User)= AerospikeKey(namespace, setName, obj.email).digest

  val objWrite : Seq[AerospikeBinProto[User, _]] = Seq(
    ("email", (u: User) => u.email),
    ("firstName", (u: User) => u.firstName),
    ("lastName" , (u: User) => u.lastName)
  )

  val objRead  =
    (key: AerospikeKey[String], record: AerospikeRecord) =>
      User(
        email = record.get("email"),
        firstName = record.get("firstName"),
        lastName = record.get("lastName")
      )

}