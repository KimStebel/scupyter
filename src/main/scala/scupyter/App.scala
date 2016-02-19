package scupyter

import scala.scalajs.js.JSApp
import org.scalajs.dom.{XMLHttpRequest, Event, document, WebSocket, MessageEvent}
import upickle.default._
import api.Json._
import api.Session._

object App extends JSApp {
  
  def appendPre(text: String): Unit = {
    val parNode = document.createElement("pre")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    document.body.appendChild(parNode)
  }
  
  def main {
    req("GET", "https://tmpnb.kimstebel.com:8000/api/stats")((xhr: XMLHttpRequest) => (e: Event) => {
      val status = read[Status](xhr.responseText)
      appendPre(status.toString)
      req("POST", "https://tmpnb.kimstebel.com:8000/api/spawn", Some(write(Image(status.container_image))))((xhr:XMLHttpRequest) => (e: Event) => {
        val url = read[Url](xhr.responseText).url
        appendPre(url)
        req("POST", s"https://tmpnb.kimstebel.com:8000/$url/api/kernels", Some(write(KernelRequest("javascript"))))((xhr:XMLHttpRequest) => (e: Event) => {
          val kernel = read[Kernel](xhr.responseText)
          appendPre(kernel.toString)
          val session = "EE3EABD3DAE84D20815648601E39BBD1"
          val wsurl = s"wss://tmpnb.kimstebel.com:8000/$url/api/kernels/${kernel.id}/channels?session_id=${session}"
          val socket = new WebSocket(wsurl)
          val req = write(execReq("console.log('hello')", session))
          println(req)
          socket.onmessage = {
            (e: MessageEvent) =>
              appendPre(read[ExecReply](e.data.toString).toString)
              appendPre(e.data.toString)
          }
          socket.onopen = (e: Event) => {
            socket.send(req)
            
          }
        })
      })
    })
  }
}

