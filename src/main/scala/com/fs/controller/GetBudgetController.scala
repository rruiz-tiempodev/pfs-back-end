package com.fs.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, GET, Produces}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class GetBudgetController(implicit executionContext: ExecutionContext) {
  implicit val timeout: Timeout = Timeout(2.seconds)

  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Get a budget", description = "Add integers",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
  ))
  def getBudgetRoute: Route =
    pathPrefix("budget") {
      get {
        (path(Segment) | parameter(Symbol("id"))) { id =>
          //val playerOptionFuture = (game ? GetPlayerByName(name)).mapTo[Option[Player]]
          complete(StatusCodes.OK)
        }
      }
    }

  val route: Route = getBudgetRoute
}

