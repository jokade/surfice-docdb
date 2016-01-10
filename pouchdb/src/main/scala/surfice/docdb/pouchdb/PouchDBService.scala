//     Project: surfice-docdb
//      Module: pouchdb
// Description: Implementation of DBService for PouchDB

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb

import surfice.docdb.DBService.{Props, DBHandle, Doc}
import surfice.docdb.{DictionaryPropsDoc, PropsDoc, DB, DBService}

import scala.language.implicitConversions
import scala.scalajs.js
import scalajs.js.JSConverters._

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

  override def get(id: String, db: PouchDB): Unit = completeWithNativeDoc {
    db.get(id)
  }

  override def getProps(id: String, db: PouchDB): Unit = completeWithProps {
    db.get(id)
  }

  override def put(doc: js.Object, db: PouchDB): Unit = completeWith {
    db.put(doc)
  }

  override def putProps(doc: PropsDoc, db: PouchDB): Unit = put( propsToNative(doc), db )

  override def openDB(url: String): Unit = {
    if(!_initialized)
      PouchDB.init()
    request ! DBHandle( new PouchDBHandle(new PouchDB(url)) )
  }

  private def propsToNative(props: PropsDoc): js.Object = props match {
    case DictionaryPropsDoc(dict) => dict
    case p =>
      val dict = p.toMap.toJSDictionary
      p.id.foreach( id => dict("_id") = id)
      dict
  }

  private def nativeToProps(doc: js.Any): PropsDoc = DictionaryPropsDoc(doc.asInstanceOf[js.Dictionary[js.Any]])

  @inline
  private def completeWith(p: PouchPromise) : Unit = p.andThen(
    (data:js.Any) => { request ! data },
    (err:PouchError) => { request.failure(PouchException(err)) }
  )

  @inline
  private def completeWithNativeDoc(p: PouchPromise) : Unit = p.andThen(
    (data:js.Any) => { request ! Doc( data ) },
    (err:PouchError) => { request.failure(PouchException(err))}
  )

  @inline
  private def completeWithProps(p: PouchPromise): Unit = p.andThen(
    (data:js.Any) => { request ! Props( nativeToProps(data) ) },
    (err:PouchError) => { request.failure(PouchException(err)) }
  )

}

case class PouchException(msg: String) extends RuntimeException(msg)
object PouchException {
  def apply(err: PouchError) : PouchException = PouchException(err.message)
}
