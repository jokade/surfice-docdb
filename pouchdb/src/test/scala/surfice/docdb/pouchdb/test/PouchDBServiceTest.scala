//     Project: surfice-docdb
//      Module: pouchdb
// Description: Unit tests for PouchDBService

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb.test

import surf.ServiceRef
import surfice.docdb.DB
import surfice.docdb.pouchdb.{PouchDBService, PouchDBHandle}
import surfice.docdb.test.DBServiceBehaviour

import scala.concurrent.ExecutionContext
import scala.scalajs.js

object PouchDBServiceTest extends DBServiceBehaviour with DBProvider {
  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.runNow

  override def createDBService(): ServiceRef = surf.ServiceRefFactory.Sync.serviceOf(new PouchDBService)

  override def createDoc(id: String, props: (String, Any)*): Any = {
    val o = js.Dictionary[js.Any]("_id"->id)
    props foreach {
      case (k,v:String) =>  o(k) = v
    }
    o
  }

  override def checkDoc(doc: Any, id: String, props: (String, Any)*): Boolean = {
    val d = doc.asInstanceOf[js.Dynamic]
    d._id.asInstanceOf[String] == id
  }

  override def createDB(): DB = new PouchDBHandle(newDB())
}
