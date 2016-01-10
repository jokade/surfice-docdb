//     Project: surfice-docdb
//      Module: pouchdb / test
// Description: Tests for the PouchDB façade

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb.test

import surfice.docdb.pouchdb.{PouchDB, PouchDBOptions}
import surfice.docdb.test.TestBase
import utest._
import scala.scalajs.js.UndefOr
import scala.util.Success
import scalajs.concurrent.JSExecutionContext.Implicits.runNow

import scala.scalajs.js

object PouchDBTests extends TestBase with DBProvider {

  override val tests = TestSuite {

    val db = newDB()

    'putAndGet-{
      val d1 = js.Dictionary("_id"->"d1", "hello" -> "world")
      db.put(d1)

      val f1 = db.get("d1").future[js.Dictionary[String]] flatMap {
        case data =>
          assert( data("hello") == "world" )
          data("hello") = "test"
          db.put(data, "d1", data("_rev")).future[js.Any]
      } flatMap {
        case _ =>
          db.get("d1").future[js.Dictionary[String]].map{
            case data => assert( data("hello") == "test" )
          }
      }

      val f2 = expectFailure( db.get("d2").future[js.Any] )

      merge(f1,f2)
    }

  }
}

trait DBProvider {
  import js.Dynamic.global
  def newDB(): PouchDB = {
    if( global.PouchDB.asInstanceOf[js.UndefOr[js.Dynamic]].isEmpty)
      global.PouchDB = global.require("pouchdb")
    new PouchDB("testdb",PouchDBOptions(db = global.require("memdown")))
  }
}
