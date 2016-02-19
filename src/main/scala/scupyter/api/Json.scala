package scupyter.api

import upickle.default._


object Json {
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

  
  
  
  
  //HTTP API data model
  case class Status(available: Int, container_image: String, version: String, capacity: Int)
  case class Kernel(id: String, name: String)
  case class KernelRequest(name:String)
  case class Url(url: String)
  case class Image(image_name:String)
  //websocket API data model
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

                     
  def execReq(code:String, session:String):WsReq = {
    WsReq(Header(newmsgid, session, "execute_request", "5.0", "username"),
          Content(code, false, true, UserExpressions(), true, true),
          Metadata(),
          ParentHeader(),
          Seq(),
          "shell")
  }
  
  var _msgid = 0
  def newmsgid = {
    val res = _msgid.toString
    _msgid += 1
    res
  }
  
  
  
  
}