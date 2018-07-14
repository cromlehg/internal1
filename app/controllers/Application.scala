package controllers

import javax.inject._
import play.api.mvc._
import java.io._
import scala.io.Source

import models.Message
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._

class Application  @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents)
  extends AbstractController(components) {

  val SendMsgForm = Form(mapping("Name" -> nonEmptyText,
    "Message" -> text)(Message.apply)(Message.unapply))
 
  def index = Action {
    Ok(views.html.index("Ok"))
  }

  def viewHello(content: String) = Action {
    Ok(views.html.hello("Just hello")(content))
  }

  def viewBad = Action {
    Ok(views.html.bad("Bad"))
  }
 
  def viewSendService = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.sendService(SendMsgForm)("Send please"))
  }

  def viewShowService(allmessages: String) = Action {
    Ok(views.html.showService("Messages")(allmessages))
  }

  var fileCounter = 0
  var message = ""

  def sendMessage = Action { implicit request =>
    SendMsgForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.bad("Bad")),
      msgForm => {

      val filePath = "txt/textFile" + fileCounter.toString + ".txt"
      fileCounter += 1
      message = "\r\nMessage number " + fileCounter.toString + " from " + msgForm.name + ": \r\n" + msgForm.txt
      val writer = new PrintWriter(new File(filePath))
      writer.write(message)
      writer.close()

      Redirect(routes.Application.viewHello(message))
    })
  }

  def viewMsgs = Action {
  	var allMessages = "\r\n"
  	var i = 0
  	for (i <- 0 to fileCounter - 1) {
  	  val filePath = "txt/textFile" + i.toString + ".txt"
  	  
	  for (line <- Source.fromFile(filePath).getLines) {
        allMessages += line + "\r\n"
	  }
	}
  Redirect(routes.Application.viewShowService(allMessages))
  }

}
