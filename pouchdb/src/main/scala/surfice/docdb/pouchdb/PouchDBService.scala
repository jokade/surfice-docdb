//     Project: surfice-docdb
//      Module: pouchdb
// Description: Implementation of DBService for PouchDB

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb

import surfice.docdb.DBService.{DocList, Props, DBHandle, Doc}
import surfice.docdb.{DictionaryPropsDoc, PropsDoc, DB, DBService}

import scala.collection.AbstractSeq
import scala.language.implicitConversions
import scala.scalajs.js
import scalajs.js.JSConverters._

final class PouchDBService extends DBService[PouchDB,PouchDoc,String] {
  import PouchDBService._

  override def checkDBHandle(db: DB): PouchDB = db match {
    case p: PouchDBHandle => p.js
    case _ => throw PouchException(s"PouchDBService cannot handle '$db'")
  }
  override implicit def checkDoc(doc: Any): PouchDoc = doc.asInstanceOf[PouchDoc]
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

  override def getAll(prefix: Option[String], db: PouchDB): Unit =
    if(prefix.isDefined) completeWithNativeList( db.allDocs(AllDocsOptions(
      startkey = prefix.get, endkey = prefix.get + "\uffff"
    )))
    else completeWithNativeList( db.allDocs() )


  override def getAllProps(prefix: Option[String], db: PouchDB): Unit = ???

  override def put(doc: PouchDoc, update: Boolean, db: PouchDB): Unit =
    if(update) completeWith{ db.put(doc, docRev = doc._rev.get)}
    else completeWith { db.put(doc) }

  override def putProps(doc: PropsDoc, db: PouchDB): Unit = put( propsToNative(doc), false, db )

  override def openDB(url: String): Unit = {
    PouchDB.init()
    request ! DBHandle( new PouchDBHandle(new PouchDB(url)) )
  }

  private def propsToNative(props: PropsDoc): PouchDoc = props match {
    case DictionaryPropsDoc(dict) => dict
    case p =>
      val dict = p.toMap.toJSDictionary
      p.id.foreach( id => dict("_id") = id)
      dict.asInstanceOf[PouchDoc]
  }


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

  @inline def completeWithNativeList(p: PouchPromise) : Unit = p.andThen(
    (data:js.Any) => { request ! DocList( new NativeIterable( data.asInstanceOf[ListResponse].rows ) ) },
    (err:PouchError) => { request.failure(PouchException(err))}
  )

  @inline
  private def completeWithProps(p: PouchPromise): Unit = p.andThen(
    (data:js.Any) => { request ! Props( nativeToProps(data) ) },
    (err:PouchError) => { request.failure(PouchException(err)) }
  )

}

object PouchDBService {
  @inline
  def nativeToProps(doc: js.Any): PropsDoc = DictionaryPropsDoc(doc.asInstanceOf[js.Dictionary[js.Any]])
}

case class PouchException(msg: String) extends RuntimeException(msg)
object PouchException {
  def apply(err: PouchError) : PouchException = PouchException(err.message)
}

final class NativeIterable(docs: js.Array[ListResponseEntry]) extends AbstractSeq[Any] {
  @inline
  override def length: Int = docs.length
  @inline
  override def apply(idx: Int): Any = docs(idx).doc.get
  @inline
  override def iterator: Iterator[Any] = docs.map( _.doc.get ).iterator
}