package com.fs.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.BackoffOpts.onFailure
import akka.pattern.ask
import akka.util.Timeout
import com.fs.CustomJsonProtocols
import com.fs.actor.BudgetActor.GetBudgetByMonth
import com.fs.entity.{Budget, IncomeExpenseType}
import com.fs.entity.IncomeExpenseType.IncomeExpenseType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, GET, Path, Produces}

import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
@Path("/budget")
case class BudgetController(budgetActor: ActorRef)(implicit executionContext: ExecutionContext) extends CustomJsonProtocols {
  implicit val timeout: Timeout = Timeout(10.seconds)

  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get a budget by month and type", description = "Get a budget by month and type",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def getByWeeklyExpensesByMonthRoutes: Route =
    pathPrefix("by-month") {
      get {
        parameters(Symbol("year").as[Int], Symbol("month").as[Int], Symbol("zone").as[String], Symbol("type").as[String]) {
          (year:Int, month: Int, zone: String, ieType: String) =>
          val zoneId = ZoneId.SHORT_IDS.get(zone)
          val zonedMonth = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of(zoneId))
          val future = (budgetActor ? GetBudgetByMonth(zonedMonth, IncomeExpenseType.withName(ieType))).mapTo[List[Budget]]

          complete(StatusCodes.OK, future)
        }
      }
    }

  val route: Route = pathPrefix("budget") { getByWeeklyExpensesByMonthRoutes }
}

