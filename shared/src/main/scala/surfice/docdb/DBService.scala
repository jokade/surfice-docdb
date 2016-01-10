//     Project: surfice-docdb
//      Module: common / shared
// Description: Base class for DBServiceS and message types for communication with DBServiceS

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb

import surf.Service
import surf.Service.Processor
import surfice.docdb.DBService.{Get, Put, OpenDB}

import scala.language.implicitConversions

abstract class DBService[DBHandleType, DocType, IdType] extends Service {

  implicit def checkDBHandle(db: DB): DBHandleType
  implicit def checkDoc(doc: Any): DocType
  implicit def checkId(id: Any): IdType

  def openDB(url: String): Unit
  def get(id: IdType, db: DBHandleType): Unit
  def put(doc: DocType, db: DBHandleType): Unit

  override val process: Processor = {
    case OpenDB(url) if isRequest => openDB(url)
    case m: Get      if isRequest => get(m.id,m.db)
    case m: Put                   => put(m.doc,m.db)
  }
}

/**
 * Defines the message types for DBServiceS
 */
object DBService {
  case class OpenDB(url: String)

  case class DBHandle(db: DB)

  case class Get(id: Any)(implicit val db: DB)

  case class Put(doc: Any)(implicit val db: DB)

  case class NativeDoc(doc: Any)
}
