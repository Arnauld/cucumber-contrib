package cucumber.contrib.grammar.pdf.stepdefs.euronext;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MatchingUnitStepDefinitions {

    @Given("^that trading mode for security is \"([^\"]*)\" and phase is \"([^\"]*)\"$")
    public void that_trading_mode_for_security_is_and_phase_is(String tradingMode, String phase) throws Throwable {
    }

    @Given("^that reference price is ([0-9]*\\.?[0-9]+)$")
    public void that_reference_price_is_(double price) throws Throwable {
    }

    @Given("^the following orders are submitted in this order:$")
    public void the_following_orders_are_submitted_in_this_order(DataTable orderTable) throws Throwable {
    }

    @When("^class auction completes$")
    public void class_auction_completes() throws Throwable {
    }

    @Then("^the calculated IMP is:$")
    public void the_calculated_IMP_is(List<Double> imp) {
    }

    @Then("^the following trades are generated:$")
    public void the_following_trades_are_generated(DataTable expectedTradesTable) throws Throwable {
    }

    @Then("^no trades are generated$")
    public void no_trades_are_generated() throws Throwable {
    }

    @Then("^the book is empty$")
    public void the_book_is_empty() throws Throwable {
    }

    @Then("^the book looks like:$")
    public void the_book_looks_like(DataTable expectedBooks) throws Throwable {
    }
}
