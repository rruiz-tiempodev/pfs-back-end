package com.fs

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.fs.actor.{BudgetActor, FixedExpenseActor, IncomeActor}
import com.fs.actor.FixedExpenseActor.AddFixedExpense
import com.fs.actor.IncomeActor.AddIncome
import com.fs.controller.{BudgetController, CurrencyExchangeController, ExpenseController, FixedExpenseController, IncomeController}
import com.fs.swagger.SwaggerDocService

import java.util.UUID.randomUUID
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object PersonalFinancialServiceApp extends App {
  implicit val system: ActorSystem = ActorSystem("PersonalFinancialServiceApp")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val fixedExpenseActor: ActorRef = system.actorOf(Props[FixedExpenseActor], "FixedExpenseActor")
  val incomeActor: ActorRef = system.actorOf(Props[IncomeActor], "IncomeActor")
  val budgetActor: ActorRef = system.actorOf(Props[BudgetActor], "BudgetActor")

  val routes = cors() (pathPrefix("api") {
            BudgetController(budgetActor).route ~
            ExpenseController(null).routes ~
            FixedExpenseController(fixedExpenseActor).routes ~
            IncomeController(incomeActor).routes ~
              CurrencyExchangeController().routes ~
            SwaggerDocService.routes
  })

  LocalUtils.fixedExpenses.foreach(expense => fixedExpenseActor !  AddFixedExpense(expense))
  LocalUtils.incomes.foreach(income => incomeActor ! AddIncome(income))


  val f = for {
    _ <- Http().newServerAt("0.0.0.0", 8080).bind(routes)
    waitOnFuture  <- Future.never
  } yield waitOnFuture

  Await.ready(f, Duration.Inf)
}
