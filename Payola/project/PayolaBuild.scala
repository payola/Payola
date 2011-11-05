import sbt._
import Keys._

object PayolaBuild extends Build 
{
    lazy val payola = Project(id = "Payola", base = file(".")).aggregate(
        helloWorld
    )

    lazy val helloWorld = Project(id = "HelloWorld", base = file("HelloWorld"))
}  