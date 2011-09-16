package json.collection

import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import net.liftweb.json.{JsonParser => Parser}
import java.io.Reader

class JsonParser {
  def parse(reader: Reader) = {

    val value = Parser.parse(reader, true)
    value.extract()
  }
}