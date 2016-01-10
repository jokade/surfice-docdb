//     Project: surfice-docdb
//      Module: pouchdb
// Description: Scala.js façade traits for PouchDB.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSName

@JSName("PouchDB")
@js.native
class PouchDB extends js.Object {
  def this(path: String, options: PouchDBOptions = null) = this()
  var _db_name: String = js.native
  def put(doc: js.Any) : Unit = js.native
}

object PouchDB {
  import js.Dynamic.{global => g}

  type Callback = js.Function2[UndefOr[js.Dynamic],UndefOr[js.Dynamic],_]

  def init(): Unit = {
    if( g.PouchDB.asInstanceOf[js.UndefOr[PouchDB]].isEmpty ) {
      // load via require
      g.PouchDB = g.require("pouchdb")
    }
  }
}

@js.native
trait PouchDBOptions extends js.Object

object PouchDBOptions {
  def apply(db: js.Dynamic = null) : PouchDBOptions = {
    val opts = js.Object().asInstanceOf[js.Dynamic]
    if(db!=null)
      opts.db = db
    opts.asInstanceOf[PouchDBOptions]
  }
}
