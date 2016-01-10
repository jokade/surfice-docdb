//     Project: surfice-docdb
//      Module: pouchdb / test
// Description: Tests for the PouchDB façade

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb.test

import surfice.docdb.pouchdb.{PouchDBOptions, PouchDB}
import surfice.docdb.test.TestBase

import utest._

import scala.scalajs.js

object PouchDBTests extends TestBase with DBProvider {

  override val tests = TestSuite {
    val db = newDB()
    println(db._db_name)

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
