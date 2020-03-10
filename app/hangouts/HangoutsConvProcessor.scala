package hangouts

import scala.util.parsing.json.JSON

class HangoutsConvProcessor {

  def processHangoutsJson() = {

    val jsonFile: String = "/Users/gkitela/Development/XLSpaceShipsPlay/resources/Hangouts.json"
    val jsonContent: String = scala.io.Source.fromFile(jsonFile).mkString
    val parsedJson = JSON.parseFull(jsonContent)
    val bindings: Map[String, String] = parsedJson.get.asInstanceOf[Map[String, String]]

    bindings
  }
}

