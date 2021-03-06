/**
 * Copyright (C) 2013 Typesafe <http://typesafe.com/>
 */
package controllers

import play.api.mvc.{ WebSocket, Controller }
import akka.actor.{ ActorRef, ActorSystem }
import com.typesafe.config.Config
import console.ConsolePlugin
import play.api.libs.json.JsValue

object ConsoleController extends ConsoleController {
  /**
   * Connects console websocket.
   */
  def connectConsole(id: String) = snap.WebSocketUtil.socketCSRFCheck {
    WebSocket.async[JsValue] { req =>
      console.ClientController.join(id)
    }
  }
}

trait ConsoleController extends Controller {
  def plugin(implicit app: play.api.Application): ConsolePlugin =
    app.plugin(classOf[ConsolePlugin]).getOrElse(throw new RuntimeException("The Console plugin does not exist"))

  def config(implicit app: play.api.Application): Config = plugin.config

  def actorSystem(implicit app: play.api.Application): ActorSystem = plugin.actorSystem

  def clientHandlerActor(implicit app: play.api.Application): ActorRef = plugin.clientHandlerActor
}
