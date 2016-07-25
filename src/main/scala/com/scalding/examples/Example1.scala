package com.scalding.examples

import com.twitter.scalding._

/**
 * Represents the Associate
 */
case class Associate(empno: Int, empname: String, designation: String, salary: Double) {
  override def toString = s"${empno}|${empname}|${designation}|${salary}"
  def toTuple: (Int, String, String, Double) = { (empno, empname, designation, salary) }
}

/*
 * Companion object for Associate
 */
object Associate {
  def fromTuple(t : (Int, String, String, Double)) : Associate = Associate(t._1, t._2, t._3, t._4)
}

/*
 * Job to filter associates whose designation is "engineer"
 *
 * @param input Path to Pipe Separated input files in HDFS
 * @param output Output directory
 */

class Example1(args: Args) extends Job(args) {
  import TDsl._

  val input = args("input")
  val output = args("output")

  val inputRecords: TypedPipe[Associate] =
    TypedPsv[(Int, String, String, Double)](input, ('empno, 'empname, 'designation, 'salary))
      .map (Associate.fromTuple(_))

  val engineers = inputRecords
    .filter(_.designation == "engineer")
    .map(_.toString)

  engineers.write(TypedPsv[String](output))
}
