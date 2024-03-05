package com.fs.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.fs.actor.IncomeActor.{AddIncome, GetIncome, GetIncomes, IncomeAdded, UpdateIncome}
import com.fs.entity.Income
import com.fs.swagger.SwaggerDocService.handleExceptions
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs._
import jakarta.ws.rs.core.MediaType

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Path("income")
case class IncomeController(actor: ActorRef)(implicit executionContext: ExecutionContext) extends CustomJsonProtocols {
  implicit val timeout: Timeout = Timeout(2.seconds)

  val exceptionHandler = ExceptionHandler {
    case ex =>
      println(s"Not such element! $ex")
      complete(StatusCodes.NotFound)
  }

  @GET
  @Path("{id}")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get an income", description = "Gets an Income by its id",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Income Not Found")
    ))
  def getRoute: Route =
    handleExceptions(exceptionHandler) {
      get {
        (path(Segment) | parameter(Symbol("id"))) { id =>
          val incomeFuture = (actor ? GetIncome(id)).mapTo[Income]
          complete(incomeFuture)
        }
      }
    }



  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get all Incomes", description = "Gets all Incomes",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Income Not Found")
    ))
  def getAllRoute: Route =
    handleExceptions(exceptionHandler) {
      get {
          val incomesFuture = (actor ? GetIncomes).mapTo[List[Income]]
          complete(incomesFuture)
        }
      }

  @PUT
  @Path("{id}")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "PUT", summary = "Updates an Income", description = "Update a specific Income by its id",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Income Not Found")
    ))
  def updateRoute: Route =
    handleExceptions(exceptionHandler) {
      put {
        (path(Segment) & entity(as[Income])) { (id, expense) =>
          actor ! UpdateIncome(id, expense)
          complete(StatusCodes.OK)
        }
      }
    }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "POST", summary = "Creates an Income", description = "",
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def postRoute: Route =
    handleExceptions(exceptionHandler) {
      post {
        pathEndOrSingleSlash {
          entity(as[Income]) { income =>
            val response = (actor ? AddIncome(income)).mapTo[IncomeAdded]
            complete(StatusCodes.Created, response)
          }
        }
      }
    }

  val routes: Route = pathPrefix("income") { getRoute ~ getAllRoute ~ postRoute ~ updateRoute}
}
