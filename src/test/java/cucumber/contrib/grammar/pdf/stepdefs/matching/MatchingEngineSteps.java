package cucumber.contrib.grammar.pdf.stepdefs.matching;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MatchingEngineSteps {
    @When("^the following orders are submitted in this order:$")
    public void the_following_orders_are_submitted_in_this_order(java.util.List<OrderRow> orders) {
    }

    @Then("^market order book looks like:$")
    public void market_order_book_looks_like(DataTable book) {
    }

    @Then("^the following trades are generated:$")
    public void the_following_trades_are_generated(java.util.List<Trade> trades) {
    }

    @Then("^no trades are generated$")
    public void no_trades_are_generated() {
    }

    @Given("^the reference price is set to \"([^\"]*)\"$")
    public void the_reference_price_is_set_to(double price) {
    }

    @Then("^the reference price is reported as \"([^\"]*)\"$")
    public void the_reference_price_is_reported_as(double price) {
    }

    @Then("^the following orders are rejected:$")
    public void the_following_orders_are_rejected(java.util.List<OrderRow> orders) {
    }
}
