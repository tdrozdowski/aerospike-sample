package models

import eu.unicredit.reactive_aerospike.data.{AerospikeBinProto, AerospikeKey, AerospikeRecord}
import eu.unicredit.reactive_aerospike.model.Dao
import eu.unicredit.reactive_aerospike.model.experimental._
import eu.unicredit.reactive_aerospike.data.AerospikeValue._
import play.api.libs.json._
import play.api.libs.json.Reads._

import scala.reflect.ClassTag


/**
 * User model & DAO
 *
 * Created by terry on 3/15/15.
 */
case class User(id : AerospikeKey[String], firstName : String, lastName : String, email : String) extends EqualUser

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

object Implicits {
  // From Play 2.4...
  implicit object ByteReads extends Reads[Byte] {
    def reads(json: JsValue) = json match {
      case JsNumber(n) if n.isValidByte => JsSuccess(n.toByte)
      case JsNumber(n) => JsError("error.expected.byte")
      case _ => JsError("error.expected.jsnumber")
    }
  }

  implicit object ByteWrites extends Writes[Byte] {
    def writes(o: Byte) = JsNumber(o)
  }
}

object UserDao extends Dao[String, User] {
  import Implicits._

  val namespace = "test"
  val setName = "users"

  def getKeyDigest(obj: User)= Dao.macroKeyDigest[User](obj)

  val objWrite : Seq[AerospikeBinProto[User, _]]  = Dao.macroObjWrite[User]

  val objRead : (AerospikeKey[String], AerospikeRecord) => User = Dao.macroObjRead[User][String]

  implicit def aerospikeKeyWrites[T: ClassTag]: Writes[AerospikeKey[T]] with Object {def writes(ak: AerospikeKey[T]): JsArray} = new Writes[AerospikeKey[T]] {
    def writes(ak : AerospikeKey[T]) = JsArray(ak.digest.map(b => Json.toJson(b)).toList)
  }

  implicit def aerospikeKeyReads[T: ClassTag](implicit keyConverter: AerospikeValueConverter[T]) : Reads[AerospikeKey[T]] = new Reads[AerospikeKey[T]] {
    def reads(json : JsValue) = json match {
      case JsString(s) =>
        try {
          new JsSuccess(AerospikeKey(namespace, setName, s.getBytes)(keyConverter))
        } catch {
          case err : Throwable => JsError(s"validation error: ${err.getMessage}")
        }

      case _ => JsError("error.expected.jsstring")
    }
  }

  implicit val userFormat = Json.format[User]

}