//     Project: surfice-docdb
//      Module: pouchdb
// Description: Implementation of DBService for PouchDB

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb

import surfice.docdb.{DB, DBService}
import surfice.docdb.DBService.{DBHandle, OpenDB}

import scala.scalajs.js
import scala.language.implicitConversions

final class PouchDBService extends DBService[PouchDB,js.Object,String] {

  private var _initialized = false

  override def checkDBHandle(db: DB): PouchDB = db match {
    case p: PouchDBHandle => p.js
    case _ => throw PouchException(s"PouchDBService cannot handle '$db'")
  }
  override implicit def checkDoc(doc: Any): js.Object = doc.asInstanceOf[js.Object]
  override implicit def checkId(id: Any): String = id match {
    case s: String => s
    case _ => throw PouchException(s"Invalid id for PouchDB: '$id'")
  }

  override def get(id: String, db: PouchDB): Unit = completeWith {
    db.get(id)
  }

  override def put(doc: js.Object, db: PouchDB): Unit = completeWith {
    db.put(doc)
  }

  override def openDB(url: String): Unit = {
    if(!_initialized)
      PouchDB.init()
    request ! DBHandle( new PouchDBHandle(new PouchDB(url)) )
  }

  @inline
  private def completeWith(p: PouchPromise) : Unit = p.andThen(
    (data:js.Any) => { request ! data },
    (err:PouchError) => {throw PouchException(err)}
  )


}

case class PouchException(msg: String) extends RuntimeException(msg)
object PouchException {
  def apply(err: PouchError) : PouchException = PouchException(err.message)
}
