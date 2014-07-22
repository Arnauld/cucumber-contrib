package cucumber.contrib.grammar.pdf.stepdefs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CurrencyStepDefs {
    @Given("^a currency named \"([^\"]*)\"$")
    public void a_currency_named(String arg1) throws Throwable {
    }

    @Given("^one \"([^\"]*)\" is worth (\\d+) \"([^\"]*)\"$")
    public void one_is_worth(String arg1, int arg2, String arg3) throws Throwable {
    }

    @When("^I convert (\\d+) \"([^\"]*)\" to \"([^\"]*)\"$")
    public void I_convert_to(int arg1, String arg2, String arg3) throws Throwable {
    }

    @Then("^the result should be (\\d+)$")
    public void the_result_should_be(int arg1) throws Throwable {
    }

    @Given("^I have a fresh quotation table$")
    public void I_have_a_fresh_quotation_table() throws Throwable {
    }

    @Then("^a new quotation from \"([^\"]*)\" to \"([^\"]*)\" should be added to the table: \"([^\"]*)\"$")
    public void a_new_quotation_from_to_should_be_added_to_the_table(String arg1, String arg2, String arg3) throws Throwable {
    }

    @Then("^I should receive an error of conversion$")
    public void I_should_receive_an_error_of_conversion() throws Throwable {
    }
}
