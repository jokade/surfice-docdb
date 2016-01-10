//     Project: surfice-docdb
//      Module: common / shared / test
// Description: Common base trait for tests

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.docdb.test

import utest._

import scala.concurrent.{ExecutionContext, Future}

trait TestBase extends TestSuite {

  case object ExpectedFailure extends RuntimeException

  def expectFailure(f: Future[_])(implicit ec: ExecutionContext): Future[Any] =
    f.map( _ => throw ExpectedFailure).recoverWith{
      case ExpectedFailure => Future.failed(ExpectedFailure)
      case ex:Throwable => Future.successful(ex)
    }

  def merge(fs: Future[Any]*)(implicit ec: ExecutionContext): Future[Any] = Future.fold(fs)("")((s,r)=>s+"["+r.toString+"]")
}
