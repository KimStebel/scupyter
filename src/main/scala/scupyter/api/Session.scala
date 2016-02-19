package scupyter.api

import org.scalajs.dom.{XMLHttpRequest, Event, WebSocket, MessageEvent}

class Session {
  val id = Session.randomId
  def exec(code:String)(cb:(Either[String, String]) => Unit) {
    
  }
}

object Session {
  def apply(language:String):Session = {
    ??? 
  }
  
  def randomID = ???
  
  def req(method:String, url:String, body:Option[String] = None)(onSuccess: XMLHttpRequest => Event => Unit) = {
    val x = new XMLHttpRequest()
    x.open(method, url)
    x.onload = (e:Event) => {
      if (x.status <= 299 && x.status >= 200) {
        onSuccess(x)(e)
      } else {
        println("xhr error")
        println(x.responseType)
        println(x.responseText)
      }
    }
    body match {
      case Some(b) => x.send(b)
      case _ => x.send()
    }
  }
}