//     Project: surfice-docdb
//      Module: common / shared
// Description: Trait for property documents

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb

/**
 * A properties-based representation of a DocDB document.
 */
trait PropsDoc {
  def id: Option[Any]
  def apply(key: String): Any
  def get[T](key: String): Option[T]
  def keys: Iterable[String]
  def toMap: Map[String,Any]
  def iterator: Iterator[(String,Any)]

  @inline
  final def string(key: String): String = apply(key).asInstanceOf[String]
  @inline
  final def string(key: String, default: =>String): String = get[String](key).getOrElse(default)
  @inline
  final def int(key: String): Int = apply(key).asInstanceOf[Int]
  @inline
  final def int(key: String, default: =>Int): Int = get[Int](key).getOrElse(default)
  @inline
  final def bool(key: String): Boolean = apply(key).asInstanceOf[Boolean]
  @inline
  final def bool(key: String, default: =>Boolean): Boolean = get[Boolean](key).getOrElse(default)
}

object PropsDoc {
  @inline
  def apply(id: Option[Any], props: Iterable[(String,Any)]): PropsDoc = new MapPropsDoc(id,props.toMap)
  @inline
  def apply(props: (String,Any)*): PropsDoc = apply(None,props)
  @inline
  def apply(id: Any, props: (String,Any)*): PropsDoc = apply(Some(id),props)
}

abstract class AbstractPropsDoc extends PropsDoc

class MapPropsDoc(val id: Option[Any], map: Map[String,Any]) extends AbstractPropsDoc {
  @inline
  final override def apply(key: String): Any = map(key)
  @inline
  final override def get[T](key: String): Option[T] = map.get(key).asInstanceOf[Option[T]]
  @inline
  final override def keys: Iterable[String] = map.keys
  @inline
  override def toMap: Map[String, Any] = map
  @inline
  final override def iterator: Iterator[(String, Any)] = map.iterator
}

