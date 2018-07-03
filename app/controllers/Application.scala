package controllers

import javax.inject._
import play.api.mvc._
import java.io._
import scala.io.Source

class Application extends Controller {

  def sendMsg(txt: String) = Action {
  	val filePath = "textFile.txt";

    val writer = new PrintWriter(new File(filePath));
    writer.write(txt);
    writer.close();

    Source.fromFile(filePath).foreach {
      print
    }

    Ok("Your message is sent.")
  }

}
