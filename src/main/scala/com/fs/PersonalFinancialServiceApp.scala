package com.fs

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.fs.actor.FixedExpenseActor
import com.fs.actor.FixedExpenseActor.AddFixedExpense
import com.fs.controller.{ExpenseController, FixedExpenseController, GetBudgetController}
import com.fs.entity.{Currency, ExpenseType, FixedExpense}
import com.fs.swagger.SwaggerDocService

import java.util.UUID.randomUUID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object PersonalFinancialServiceApp extends App {
  implicit val system: ActorSystem = ActorSystem("PersonalFinancialServiceApp")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val fixedExpenseActor: ActorRef = system.actorOf(Props[FixedExpenseActor], "FixedExpenseActor")

  val routes = cors() (pathPrefix("api") {
          new GetBudgetController().route ~
          ExpenseController(null).routes ~
          FixedExpenseController(fixedExpenseActor).routes ~
          SwaggerDocService.routes
  })

 LocalUtils.fixedExpenses.foreach(expense => fixedExpenseActor !  AddFixedExpense(expense))

  val f = for {
    _ <- Http().newServerAt("0.0.0.0", 8080).bind(routes)
    waitOnFuture  <- Future.never
  } yield waitOnFuture

  Await.ready(f, Duration.Inf)
}
