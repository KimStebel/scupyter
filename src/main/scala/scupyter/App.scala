package scupyter

import scala.scalajs.js.JSApp

import org.scalajs.dom.{XMLHttpRequest, Event, document, WebSocket, MessageEvent}
import upickle.default._
    
case class Status(available: Int, container_image: String, version: String, capacity: Int)
case class Kernel(id: String, name: String)
case class KernelRequest(name:String)
case class Url(url: String)
case class Image(image_name:String)
/*
{
    "metadata": {},
    "content": {
      "name": "stdout",
      "text": "{ _id: 'zebra',\n  _rev: ..."
    },
    "channel": "iopub",
    "buffers": [],
    "msg_type": "stream",
    "parent_header": {
      "session": "EE3EABD3DAE84D20815648601E39BBD1",
      "username": "username",
      "msg_type": "execute_request",
      "version": "5.0",
      "msg_id": "9761242A018742C385A4A4AC54000A53"
    },
    "header": {
      "session": "EE3EABD3DAE84D20815648601E39BBD1",
      "date": "2016-02-13T14:38:25.657593",
      "msg_type": "stream",
      "username": "username",
      "version": "5.0",
      "msg_id": "ece9451a-203e-4949-a0f6-bbe1206d1add"
    },
    "msg_id": "ece9451a-203e-4949-a0f6-bbe1206d1add"
  }
 */
case class ExecReply(metadata:Metadata,
                     content:ExecReplyContent,
                     channel:String,
                     buffers:Seq[String],
                     msg_type:String,
                     parent_header:ParentHeader,
                     header:Header,
                     msg_id:String)
case class ExecReplyContent(name:String, text:String)

case class UserExpressions()
case class ParentHeader()
case class Metadata()
case class WsReq(header:Header,
                 content: Content,
                 metadata:Metadata,
                 parent_header:ParentHeader,
                 buffers:Seq[String],
                 channel:String)
case class Header(msg_id:String,
                  session: String,
                  msg_type: String,
                  version: String,
                  username:String)
case class Content(code:String,
                   silent:Boolean,
                   store_history:Boolean,
                   user_expressions:UserExpressions,
                   allow_stdin:Boolean,
                   stop_on_error:Boolean)


object App extends JSApp {
  def execReq(code:String, session:String) = {
    WsReq(Header(newmsgid, session, "execute_request", "5.0", "username"),
          Content(code, false, true, UserExpressions(), true, true),
          Metadata(),
          ParentHeader(),
          Seq(),
          "shell")
  }
  
  def appendPre(text: String): Unit = {
    val parNode = document.createElement("pre")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    document.body.appendChild(parNode)
  }
  
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
  
  var _msgid = 0
  def newmsgid = {
    val res = _msgid.toString
    _msgid += 1
    res
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

