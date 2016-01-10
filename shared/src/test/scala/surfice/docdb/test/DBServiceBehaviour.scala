//     Project: surfice-docdb
//      Module: common / shared
// Description: Test cases for all DBService implementations.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.test

import surf.ServiceRef
import surf.dsl._
import surfice.docdb.DB
import surfice.docdb.DBService.{Get, Put}
import utest._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

trait DBServiceBehaviour extends TestBase {
  implicit def executionContext: ExecutionContext
  def createDBService(): ServiceRef
  def createDB(): DB
  def createDoc(id: String, props: (String,Any)*): Any
  def checkDoc(doc: Any, id: String, props: (String,Any)*): Boolean

  val tests = TestSuite {
    implicit val db = createDB()
    val service = createDBService()
    'putAndGet-{
      val doc = createDoc("d1", "hello" -> "world")
      (Put(doc) >> service :: transform {
        case _ => Get("d1")
      } :: service).future.map{
        case doc =>
          assert( checkDoc(doc,"d1") )
      }
    }
  }
}
