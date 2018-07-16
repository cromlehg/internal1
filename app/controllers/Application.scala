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

  var fileCounter = 0
  var seqMsg:Seq[Message] = Seq()

  val SendMsgForm = Form(mapping("Name" -> nonEmptyText,
    "Message" -> text)(Message.apply)(Message.unapply))
 
  def index = Action {
    Redirect(routes.Application.viewShowService)
  }

  def viewHello(content: String) = Action {
    Ok(views.html.hello("Info")(content))
  }

  def viewBad = Action {
    Ok(views.html.bad("Bad"))
  }
 
  def viewSendService = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.sendService(SendMsgForm)("Send message"))
  }

  def viewShowService = Action {
    getAllMessages
    Ok(views.html.showService("Messages")(seqMsg))
  }

  def viewMessages = Action {
    Redirect(routes.Application.viewShowService)
  }

  private def getAllMessages = {
    var i = 0
    seqMsg = Seq()
    for (i <- 0 to fileCounter - 1) {
      val filePath = "txt/textFile" + i.toString + ".txt"
      var name = ""
      var txt = ""
      var k = 0
      for (line <- Source.fromFile(filePath).getLines) {
        k += 1
        if (k == 1) name += line
        else txt += line + "\r\n"
      }
      val msg = new Message(name,txt)
      seqMsg = seqMsg :+ msg
    }
  }

  def sendMessage = Action { implicit request =>
    SendMsgForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.bad("Bad")),
      msgForm => {
      var message = "" 
      var info = "" 
      val filePath = "txt/textFile" + fileCounter.toString + ".txt"
      fileCounter += 1
      info = "Message from " + msgForm.name + ": '" + msgForm.txt + "' has been sent."
      message = msgForm.name + "\r\n" + msgForm.txt
      val writer = new PrintWriter(new File(filePath))
      writer.write(message)
      writer.close()

      Redirect(routes.Application.viewHello(info))
    })
  }

}
