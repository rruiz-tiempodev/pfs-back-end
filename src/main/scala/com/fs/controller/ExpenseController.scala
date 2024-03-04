package com.fs.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.{DateTime, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, GET, Path, Produces}

import java.time.{ZoneId, ZonedDateTime}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
@Path("/expense")
case class ExpenseController(actor: ActorRef) (implicit executionContext: ExecutionContext) extends CustomJsonProtocols {
  implicit val timeout: Timeout = Timeout(2.seconds)



  @GET
  @Path("{id}")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get an expense", description = "Gets an expense by its id",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def getExpenseRoutes: Route =
      get {
        path(Segment) { id =>
          println(id)
          complete(StatusCodes.OK)
        }
    }

  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get the weekly expenses by month", description = "Get the weekly expenses by month",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def getByWeeklyExpensesByMonthRoutes: Route =
    pathPrefix("by-month") {
      get {
        parameters(Symbol("year").as[Int], Symbol("month").as[Int], Symbol("zone").as[String]) { (year:Int, month: Int, zone: String) =>
          val zoneId = ZoneId.SHORT_IDS.get(zone)
          val zonedMonth = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of(zoneId))

          complete(StatusCodes.OK, zonedMonth.toString)
        }
      }
    }

  val routes: Route = pathPrefix("expense") { getByWeeklyExpensesByMonthRoutes ~ getExpenseRoutes }
}
