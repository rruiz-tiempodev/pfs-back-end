package com.fs.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, parameters, pathPrefix}
import akka.http.scaladsl.server.Route
import com.fs.CustomJsonProtocols
import com.fs.actor.BudgetActor.GetBudgetByMonth
import com.fs.client.CurrencyExchangeClient
import com.fs.entity.{Budget, IncomeExpenseType}
import com.fs.swagger.SwaggerDocService._symbol2NR
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, GET, Path, Produces}

import scala.concurrent.{ExecutionContextExecutor}


@Path("/currency-exchange")
case class CurrencyExchangeController()(implicit system: ActorSystem, executionContext: ExecutionContextExecutor) extends CustomJsonProtocols {
  @GET
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method= "GET", summary = "Get the currency exchange", description = "Get a currency exchange",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Get response"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    ))
  def getCurrencyExchangeRoute: Route =
    get {
      parameters(Symbol("currencies").as[String]) {
        (currencies: String) =>
          val currencyExchange = new CurrencyExchangeClient().retrieveExchangeRate(currencies)
          complete(StatusCodes.OK, currencyExchange)
      }
    }

  val routes: Route = pathPrefix("currency-exchange") { getCurrencyExchangeRoute }
}
