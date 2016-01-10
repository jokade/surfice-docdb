//     Project: surfice-docdb
//      Module: common / shared
// Description: Test cases for all DBService implementations.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.test

import surf.ServiceRef
import surf.dsl._
import surfice.docdb.{PropsDoc, DB}
import surfice.docdb.DBService._
import utest._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

trait DBServiceBehaviour extends TestBase {
  implicit def executionContext: ExecutionContext
  def createDBService(): ServiceRef
  def createDB(): DB
  def createDoc(id: String, props: (String,Any)*): Any
  def checkDoc(doc: Any, id: String, props: (String,Any)*): Boolean

  val propsDoc =  PropsDoc("d2",
          "hello" -> "world",
          "int" -> 42,
          "bool" -> true,
          "double" -> 123.456D,
          "complex.key" -> "test")

  val tests = TestSuite {
    implicit val db = createDB()
    val service = createDBService()
    'putAndGet-{
      'native-{
        val doc = createDoc("d1", "hello" -> "world")
        (Put(doc) >> service :: transform {
          case _ => Get("d1")
        } :: service).future.map {
          case Doc(doc) => assert(checkDoc(doc, "d1"))
        }
      }
      'props-{
        (PutProps(propsDoc) >> service :: transform {
          case _ => GetProps("d2")
        } :: service).future.map {
          case Props(doc) => assert(
            doc("hello") == "world",
            doc("int") == 42,
            doc("bool") == true,
            doc("double") == 123.456D,
            doc("complex.key") == "test"
          )
        }
      }
    }
  }
}
