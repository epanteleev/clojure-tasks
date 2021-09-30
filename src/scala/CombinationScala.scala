package scala

import scala.annotation.tailrec

object CombinationScala {

  private def addLetter(symbol: String, words: List[String]): List[String] =
    words collect {
      case word if !word.startsWith(symbol) => word + symbol
    }

  private def step(symbols: List[String], words: List[String]): List[String] =
    symbols.foldLeft(List.empty[String]) {
      (acc, x) => acc ++ addLetter(x, words)
    }

  def combination(symbolList: List[String], n: Int): List[String] = {
    @tailrec
    def iter(symbolList: List[String], words: List[String], n: Int): List[String] = {
      if (n > 1)
        iter(symbolList, step(symbolList, words), n - 1)
      else
        words
    }

    iter(symbolList, symbolList, n)
  }

  def main(args: Array[String]): Unit = {
    println(combination("a" :: "b" :: List("c"), 1))
    println(combination("a" :: "b" :: List("c"), 2))
  }
}
