package json.collection

import java.net.URI

case class JsonCollection(version: Version = Version.ONE,
                          links: Seq[Link],
                          items: Seq[Item],
                          error: Option[ErrorMessage],
                          template: Option[Template],
                          queries: Seq[Query])

object JsonCollection {

  def apply(error: ErrorMessage):JsonCollection =
    JsonCollection(List(), List(), Some(error), None, List())

  def apply(links: Seq[Link],
            items: Seq[Item],
            template: Option[Template],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(links, items, None, template, queries)

  def apply(links: Seq[Link],
            items: Seq[Item],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(links, items, None, None, queries)

  def apply(links: Seq[Link],
            items: Seq[Item]):JsonCollection =
    JsonCollection(links, items, None, None, List())

}

sealed class Version(id:String)

object Version {
  def apply(id: String) = id match {
    case "1.0" => ONE
    case _ => sys.error("Unknown")
  }

  case object ONE extends Version("1.0")
}

case class ErrorMessage(title: String, code: String, message: String)
case class Property[A](name: String, value: Option[Value[A]])
case class Value[A](value: A)
case class Link(href: URI, rel: String, prompt: Option[String])
case class Item(href: URI, properties: Seq[Property[_]], links: Seq[Link])
case class Query(href: URI, properties: Seq[Property[_]], links: Seq[Link])
case class Template(href: URI, rel: String, prompt: Option[String], properties: Seq[Property[_]])

object Conversions {
  implicit def stringToValue(value: String)   = Some(Value(value))
  implicit def numericToValue(value: Numeric) = Some(Value(value))
  implicit def booleanToValue(value: Boolean) = Some(Value(value))

  implicit def valueToType[A](value: Option[Value[A]]) = value.map(_.value)
}
