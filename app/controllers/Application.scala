package controllers

import javax.inject._
import play.api.mvc._
import java.io._
import scala.io.Source

class Application extends Controller {
  var fileCounter = 0
  var message = ""
  def sendMsg(txt: String) = Action {
  	val filePath = "txt/textFile" + fileCounter.toString + ".txt"
  	fileCounter = fileCounter + 1
  	message = fileCounter.toString + "\r\n" + txt
    val writer = new PrintWriter(new File(filePath))
    writer.write(message)
    writer.close()

    Ok("This message is sent:\r\n" + message)
  }

  def doSmth = Action {
  	Ok("Smth is done.")
  }

  def viewMsgs = Action {
  	var allMessages = "\r\n"
  	var i = 0
  	for (i <- 0 to fileCounter - 1) {
  	  val filePath = "txt/textFile" + i.toString + ".txt"
  	  
	  for (line <- Source.fromFile(filePath).getLines) {
        allMessages = allMessages + line + "\r\n"
	  }
	}

	Ok("All messages:" + allMessages)
  }

}
