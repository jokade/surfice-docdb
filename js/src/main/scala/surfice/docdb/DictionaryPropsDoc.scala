//     Project: surfice-docdb
//      Module: common / js
// Description: A PropsDoc implementation based on a js.Dictionary

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb

import scala.scalajs.js

/**
 * A [[PropsDoc]] that wraps a [[js.Dictionary]].
 *
 * @param dict
 */
case class DictionaryPropsDoc(dict: js.Dictionary[js.Any]) extends AbstractPropsDoc {
  @inline
  final override def id: Option[Any] = dict.get("_id")
  @inline
  final override def apply(key: String): Any = dict(key)
  @inline
  final override def get[T](key: String): Option[T] = dict.get(key).asInstanceOf[Option[T]]
  @inline
  final override def keys: Iterable[String] = dict.keys
  @inline
  override def toMap: Map[String, Any] = dict.toMap
  @inline
  override def iterator: Iterator[(String, Any)] = dict.iterator
}

