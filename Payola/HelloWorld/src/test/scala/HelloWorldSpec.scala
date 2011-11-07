package payola.helloworld.tests

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import payola.helloworld._

class HelloWorldSpec extends FlatSpec with ShouldMatchers {
    "Hello World" should "show Hello World" in {
        HelloWorld.hello should equal("Hello World!")
    }
}      