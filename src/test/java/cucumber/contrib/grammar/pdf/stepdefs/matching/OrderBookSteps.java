package cucumber.contrib.grammar.pdf.stepdefs.matching;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class OrderBookSteps {

    @Given("^the following orders are added to the \"([^\"]*)\" book:$")
    public void the_following_orders_are_added_to_the_book(String side, List<OrderRow> orderTable) {
    }

    @Then("^the \"([^\"]*)\" order book looks like:$")
    public void the_order_book_looks_like(String sideString, DataTable bookTable) {
    }

    @When("^the top order of the \"([^\"]*)\" book is filled by \"([^\"]*)\"$")
    public void the_top_order_of_the_book_is_filled_by(String sideString, double qty) {
    }

    @Then("^the best limit for \"([^\"]*)\" order book is \"([^\"]*)\"$")
    public void the_best_limit_for_order_book_is(String sideString, String expectedBestLimit) {
    }

    @When("^the top order goes away from the \"([^\"]*)\" book$")
    public void the_top_order_goes_away_from_the_book(String sideString) {
    }

    @Then("^the following \"([^\"]*)\" orders are rejected:$")
    public void the_following_orders_are_rejected(String side, java.util.List<OrderRow> orderTable) {
    }

    @Then("^the following \"([^\"]*)\" orders are cancelled:$")
    public void the_following_orders_are_cancelled(String side, java.util.List<OrderRow> orderTable) {
    }
}
