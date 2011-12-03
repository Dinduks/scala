/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author  Paul Phillips
 */

package scala.tools
package util

object EditDistance {
  def similarString(name: String, allowed: TraversableOnce[String]): String = {
    val suggested = suggestions(name, allowed.toSeq, maxDistance = 2, maxSuggestions = 2)
    if (suggested.isEmpty) "" 
    else suggested.mkString(" (similar: ", ", ", ")")
  }

  def suggestions(a: String, bs: Seq[String], maxDistance: Int = 3, maxSuggestions: Int = 3): Seq[String] =
    bs.map { b => (b, distance(a, b) ) } filter (_._2 <= maxDistance) sortBy(_._2) take(maxSuggestions) map(_._1)

  def distance(a: String, b: String): Int =
    levenshtein(a, b, insertCost = 1, deleteCost = 1, subCost = 2, transposeCost = 1, matchCost = -1, true)

  /** Translated from the java version at
  *    http://www.merriampark.com/ld.htm
  *  which is declared to be public domain.
  */
  def levenshtein(s: String, t: String, insertCost: Int = 1, deleteCost: Int = 1, subCost: Int = 1, transposeCost: Int = 1, matchCost: Int = 0, transpositions: Boolean = false): Int = {
    val n = s.length
    val m = t.length
    if (n == 0) return m
    if (m == 0) return n

    val d = Array.ofDim[Int](n + 1, m + 1)
    0 to n foreach (x => d(x)(0) = x)
    0 to m foreach (x => d(0)(x) = x)

    for (i <- 1 to n ; val s_i = s(i - 1) ; j <- 1 to m) {
      val t_j   = t(j - 1)
      val cost  = if (s_i == t_j) matchCost else subCost
      val tcost = if (s_i == t_j) matchCost else transposeCost

      val c1 = d(i - 1)(j) + deleteCost
      val c2 = d(i)(j - 1) + insertCost
      val c3 = d(i - 1)(j - 1) + cost

      d(i)(j) = c1 min c2 min c3

      if (transpositions) {
        if (i > 1 && j > 1 && s(i - 1) == t(j - 2) && s(i - 2) == t(j - 1))
        d(i)(j) = d(i)(j) min (d(i - 2)(j - 2) + cost)
      }
    }

    d(n)(m)
  }
}