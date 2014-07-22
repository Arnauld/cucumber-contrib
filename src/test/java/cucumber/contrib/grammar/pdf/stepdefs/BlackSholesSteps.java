package cucumber.contrib.grammar.pdf.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class BlackSholesSteps {
    @Given("the following option:")
    public void GivenTheFollowingOption(DataTable table) {
    }

    @Given("the market rate: (.*)")
    public void GivenTheMarketRate(double rate) {
    }

    @Given("the market spot for underlying \"(.*)\": (.*)")
    public void GivenTheMarketSpotForUnderlying(String underlying, double spot) {
    }

    @Given("the market volatility for underlying \"(.*)\" since (.*): (.*)")
    public void GivenTheMarketVolatilityForUnderlying(String underlying, int dateOffsetsince, double volatility) {
    }

    @When("I compute the price")
    public void WhenIComputeThePrice() {
    }

    @When("I compute the price with market data the option:")
    public void WhenIComputeThePriceWithMarketData(DataTable table) {
    }

    @Then("the result should be (.*)")
    public void ThenTheResultShouldBe(double expected) {
    }
}
