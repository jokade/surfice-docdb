//     Project: surfice-docdb
//      Module: common / shared
// Description: Base class for DBServiceS and message types for communication with DBServiceS

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb

import surf.Service
import surf.Service.Processor
import surfice.docdb.DBService._

import scala.language.implicitConversions

abstract class DBService[DBHandleType, DocType, IdType] extends Service {

  implicit def checkDBHandle(db: DB): DBHandleType
  implicit def checkDoc(doc: Any): DocType
  implicit def checkId(id: Any): IdType

  def openDB(url: String): Unit
  def get(id: IdType, db: DBHandleType): Unit
  def getProps(id: IdType, db: DBHandleType): Unit
  def getAll(prefix: Option[IdType], db: DBHandleType): Unit
  def getAllProps(prefix: Option[IdType], db: DBHandleType): Unit
  def put(doc: DocType, update: Boolean, db: DBHandleType): Unit
  def putProps(doc: PropsDoc, db: DBHandleType): Unit

  override val process: Processor = {
    case OpenDB(url)    if isRequest => openDB(url)
    case m: Get         if isRequest => get(m.id,m.db)
    case m: GetProps    if isRequest => getProps(m.id,m.db)
    case m: GetAll      if isRequest => getAll(m.prefix.map(checkId),m.db)
    case m: GetAllProps if isRequest => getAllProps(m.prefix.map(checkId),m.db)
    case m: Put                      => put(m.doc,m.update,m.db)
    case m: PutProps                 => putProps(m.doc,m.db)
  }
}

/**
 * Defines the message types for DBServiceS
 */
object DBService {
  case class OpenDB(url: String)

  case class DBHandle(db: DB)

  /**
   * Requests the native document with the specified id.
   *
   * @param id document ID
   * @param db DB to be used for the request.
   *
   * @return [[Doc]]
   */
  case class Get(id: Any)(implicit val db: DB)

  /**
   * Requests the [[PropsDoc]] with the specified id.
   *
   * @param id document ID.
   * @param db DB to be used for the request.
   *
   * @return [[Props]]
   */
  case class GetProps(id: Any)(implicit val db: DB)

  /**
   * Request to return all documents in the DB, or all documents with the specified ID prefix.
   *
   * @param prefix Return all documents if None; otherwise return all documents matching the specified ID prefix.
   * @param db DB to be used for the request
   *
   * @return [[DocList]]
   */
  case class GetAll(prefix: Option[Any] = None)(implicit val db: DB)

  /**
   * Request to return all property documents in the DB, or all documents with the specified ID prefix.
   *
   * @param prefix Return all documents if None; otherwise return all documents matching the specified ID prefix.
   * @param db DB to be used for the request
   *
   * @return [[PropsList]]
   */
  case class GetAllProps(prefix: Option[Any])(implicit val db: DB)

  /**
   * Request to save (create or update) a native document.
   *
   * @param doc
   * @param update Set to true, if you want to update an existing document (otherwise the update will fail with an error)
   * @param db
   */
  case class Put(doc: Any, update: Boolean = false)(implicit val db: DB)

  /**
   * Request to save (create or update) a properties document.
   *
   * @param doc
   * @param db
   */
  case class PutProps(doc: PropsDoc)(implicit val db: DB)

  /**
   * Request to create a new native document, with an auto-generated ID.
   *
   * @param doc
   * @param db
   *
   * @return [[]]
   */
//  case class Post(doc: Any)(implicit val db: DB)

  /**
   * Response for an operation that returns a native document.
   *
   * @param doc native represenation of the requested Document
   */
  case class Doc(doc: Any)

  /**
   * Response for an operation that returns a [[PropsDoc]].
   *
   * @param doc [[PropsDoc]] representation of the requested document.
   */
  case class Props(doc: PropsDoc)

  /**
   * Response for an operation that returns a result list of native documents.
   *
   * @note If possible, the returned Iterable should be a lazy collection,
   *       but this might not be supported by the underlying document database.
   *
   * @param docs (lazy) collection with all requested native documents.
   */
  case class DocList(docs: Iterable[Any])

  /**
   * Response for an operation that returns a result list of property documents.
   *
   * @note If possible, the returned Iterable should be a lazy collection,
   *       but this might not be supported by the underlying document database.
   *
   * @param docs (lazy) collection with all requested property documents.
   */
  case class PropsList(docs: Iterable[PropsDoc])
}
