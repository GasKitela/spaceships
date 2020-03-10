package hangouts

import java.io.PrintWriter
import java.text.SimpleDateFormat

import jsonsupport.JSONWriteReadSupport
import org.json4s._
import org.json4s.jackson.JsonMethods
import play.api.libs.json.Json
import java.util.TimeZone

/* ----------------------- INPUT ----------------------- */

case class Conversations(conversations: List[ConversationObject])

case class ConversationObject(conversation: ConversationInfo,
                              events: List[MessageObject])

case class ConversationInfo(conversationId: ConversationId)

case class ConversationId(id: String)

case class SenderId(chatId: String)

case class MessageObject(senderId: SenderId,
                         timestamp: String,
                         chatMessage: ChatMessage)

case class ChatMessage(messageContent: MessageContent)

case class MessageContent(segment: List[SegmentItem])

case class SegmentItem(text: Option[String],
                       formatting: Option[String])

/* ----------------------- OUTPUT ----------------------- */

case class ConversationOutput(conversationId: String,
                              messages: List[MessageOutput])

case class MessageOutput(sender: String,
                         time: String,
                         msgs: List[String])

case class RandomShit(aId: String,
                      b: String,
                      c: String)

object Main extends App with JSONWriteReadSupport {

  def messageObjectToMessageOutput(mo: MessageObject) = {

    val df = new SimpleDateFormat("\"EEE, d MMM yyyy HH:mm:ss\"")
    df.setTimeZone(TimeZone.getTimeZone("GMT-03:00"))

    MessageOutput(sender = mo.senderId.chatId,
                  msgs = mo.chatMessage.messageContent.segment.map(_.text.getOrElse("")),
                  time = df.format(mo.timestamp.toLong / 1000))
  }

  def fromConversationObjectToConversationOutput(co: ConversationObject) = {
    ConversationOutput(conversationId = co.conversation.conversationId.id,
                       messages = co.events.sortBy(_.timestamp).map(messageObjectToMessageOutput))
  }

  val jsonStr = """{"a_id":"1","b":"Babu","c":"dsfjskkjfs"}"""

  val conversationFile = scala.io.Source.fromFile("/Users/gkitela/Development/XLSpaceShipsPlay/resources/conver")

  val convAsString = conversationFile.mkString

  val modelConv = JsonMethods.parse(convAsString).camelizeKeys.extract[ConversationObject]

  val outputConv = fromConversationObjectToConversationOutput(modelConv)

  val converJsoneada = Json.toJson(outputConv).toString

  Some(new PrintWriter("sarasitalasuperconver")).foreach{
    p => p.write(converJsoneada)
      p.close()}

  println("Done")



}