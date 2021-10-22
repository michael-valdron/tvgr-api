package fixtures

import org.scalatest.Assertion
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

trait MyBaseFixture {
  protected def setup(): Unit
  protected def teardown(): Unit

  protected def withSetupTeardown(test: Unit => Assertion): Assertion = {
    setup()
    try {
      test()
    } finally {
      teardown()
    }
  }

  protected def buildApp: Application = new GuiceApplicationBuilder().build()

  protected def fetchDao[D](app: Application): D = {
    val applicationToDao = Application.instanceCache[D]
    applicationToDao(app)
  }
}
