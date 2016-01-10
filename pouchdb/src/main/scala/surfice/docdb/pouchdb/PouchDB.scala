//     Project: surfice-docdb
//      Module: pouchdb
// Description: Scala.js façade traits for PouchDB.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.pouchdb

import surfice.docdb.DB

import scala.concurrent.duration.Duration
import scala.concurrent.{CanAwait, ExecutionContext, Future, TimeoutException}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName
import scala.scalajs.js.{UndefOr, undefined}
import scala.util.{Failure, Success, Try}

@JSName("PouchDB")
@js.native
class PouchDB extends js.Object {
  def this(path: String, options: PouchDBOptions = null) = this()

  var _db_name: String = js.native

  def get(docId: String): PouchPromise = js.native
  def put(doc: js.Any, docId: UndefOr[String] = undefined, docRev: UndefOr[String] = undefined, options: UndefOr[js.Object] = undefined) : PouchPromise = js.native
}

case class PouchDBHandle(js: PouchDB) extends DB

object PouchDB {
  import js.Dynamic.{global => g}

  type Callback = js.Function2[js.Dynamic,js.Dynamic,_]


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
  def apply(db: UndefOr[js.Dynamic] = undefined) : PouchDBOptions = {
    val opts = js.Object().asInstanceOf[js.Dynamic]
    if(db.isDefined)
      opts.db = db
    opts.asInstanceOf[PouchDBOptions]
  }
}

@js.native
trait PouchPromise extends js.Object {
  @JSName("then")
  def andThen[T](onSuccess: js.Function1[T,_], onFailure: js.Function1[PouchError,_]) : PouchPromise = js.native
  @JSName("then")
  def success[T](onSuccess: js.Function1[T,Unit]) : PouchPromise = js.native
  @JSName("catch")
  def failure(onFailure: js.Function1[PouchError,Unit]) : PouchPromise = js.native
}

object PouchPromise {
  implicit class RichPromise(val p: PouchPromise) extends AnyVal {
    def future[T]: Future[T] = new PouchFuture[T](p)
  }

  private class PouchFuture[T](promise: PouchPromise) extends Future[T] {
    private var _result: Option[Try[T]] = None
    override def onComplete[U](f: (Try[T]) => U)(implicit executor: ExecutionContext): Unit =
      promise.andThen[T]( (data:T) => {f( Success(data) );()},
          (err:PouchError) => { f( Failure(new RuntimeException(err.message)) ); ()} )
    override def isCompleted: Boolean = _result.isDefined
    override def value: Option[Try[T]] = ???
    @throws[Exception](classOf[Exception])
    override def result(atMost: Duration)(implicit permit: CanAwait): T = ???
    @throws[InterruptedException](classOf[InterruptedException])
    @throws[TimeoutException](classOf[TimeoutException])
    override def ready(atMost: Duration)(implicit permit: CanAwait): PouchFuture.this.type = ???
  }
}

@js.native
trait PouchError extends js.Object {
  def status: Int = js.native
  def message: String = js.native
}
