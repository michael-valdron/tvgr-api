package fixtures

import org.scalatest.Assertion
import play.api.Application

import scala.reflect.ClassTag

trait MyBaseFixture {
  protected def setup(): Unit
  protected def teardown(): Unit

  protected def withSetupTeardown(test: => Assertion): Assertion = {
    setup()
    try test finally teardown()
  }

  protected def fetchDao[D : ClassTag](app: Application): D = {
    val applicationToDao = Application.instanceCache[D]
    applicationToDao(app)
  }
}
