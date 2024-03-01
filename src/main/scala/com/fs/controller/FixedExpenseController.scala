package com.fs.controller

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.fs.actor.FixedExpenseActor.{AddFixedExpense, FixedExpenseAdded, GetFixedExpense, GetFixedExpenses, UpdateFixedExpense}
import com.fs.entity.FixedExpense
import com.fs.swagger.SwaggerDocService.handleExceptions
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{Consumes, GET, POST, PUT, Path, Produces}
import jakarta.ws.rs.core.MediaType

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
@Path("fixed-expense")
case class FixedExpenseController(actor: ActorRef)(implicit executionContext: ExecutionContext) extends CustomJsonProtocols {
  implicit val timeout: Timeout = Timeout(2.seconds)

  //val actor: ActorRef = system.actorOf(Props[FixedExpenseActor], "FixedExpenseActor")

  /*implicit val customRejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case m: InternalSer =>
        println(s"Got a method rejection $m")
        complete(StatusCodes.OK)
    }*/
  val exceptionHandler = ExceptionHandler {
    case ex =>
      println(s"Not such element! $ex")
      complete(StatusCodes.NotFound)
  }

  @GET
  @Path("{id}")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get a fixed expense", description = "Gets a fixed expense by its id",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Fixed Response Not Found")
    ))
  def getRoute: Route =
    handleExceptions(exceptionHandler) {
      get {
        (path(Segment) | parameter(Symbol("id"))) { id =>
          val fixedExpenseFuture = (actor ? GetFixedExpense(id)).mapTo[FixedExpense]
          complete(fixedExpenseFuture)
        }
      }
    }

  @PUT
  @Path("{id}")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "PUT", summary = "Updates a fixed expenses", description = "Update an specific fixed expense by its id",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Fixed Response Not Found")
    ))
  def updateRoute: Route =
    handleExceptions(exceptionHandler) {
      put {
        (path(Segment) | (parameter(Symbol("id")) & entity(as[FixedExpense]))) { (id: String, expense: FixedExpense) =>
          actor ! UpdateFixedExpense(id, expense)
          complete(StatusCodes.OK)
        }
      }
    }

  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get all fixed expenses", description = "Gets all the fixed expenses",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error"),
      new ApiResponse(responseCode = "404", description = "Fixed Response Not Found")
    ))
  def getAllRoute: Route =
    handleExceptions(exceptionHandler) {
      get {
          val fixedExpenseFuture = (actor ? GetFixedExpenses).mapTo[List[FixedExpense]]
          complete(fixedExpenseFuture)
        }
      }



  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "POST", summary = "Creates a fixed expense", description = "",
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def postRoute: Route =
    handleExceptions(exceptionHandler) {
      post {
        pathEndOrSingleSlash {
          entity(as[FixedExpense]) { fixedExpense =>
            val response = (actor ? AddFixedExpense(fixedExpense)).mapTo[FixedExpenseAdded]
            complete(StatusCodes.Created, response)
          }
        }
      }
    }

  val routes: Route = pathPrefix("fixed-expense") { getAllRoute ~ getRoute ~ postRoute}
}
