package com.scalding.examples

import com.twitter.scalding._

import org.scalatest._

class Example1Test extends WordSpec with Matchers {
  val testInput = List(
    (111,"John","engineer",4000.0),
    (222,"Mike","manager",5000.0)
  )

  "Example1 Job" should {
    import Dsl._

    JobTest(new com.scalding.examples.Example1(_))
      .arg("input", "input")
      .source(TypedPsv[(Int, String, String, Double)]("input", ('empno, 'empname, 'designation, 'salary)), testInput)
      .arg("output", "output")
      .typedSink(TypedPsv[String]("output")) { buff =>
        "filter engineers" in {
          buff.toList shouldBe List("111|John|engineer|4000.0")
        }
      }.run.finish
  }
}