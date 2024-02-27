package com.fs.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, GET, Path, Produces}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
@Path("/expense")
class ExpenseController (implicit executionContext: ExecutionContext) {
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
  def getRoutes: Route =
    pathPrefix("expense") {
      get {
        (path(Segment) | parameter(Symbol("id"))) { id =>
          //val playerOptionFuture = (game ? GetPlayerByName(name)).mapTo[Option[Player]]
          complete(StatusCodes.OK)
        }
      }
    }

  val routes: Route = getRoutes
}
