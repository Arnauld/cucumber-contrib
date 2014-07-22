package cucumber.contrib.grammar.pdf.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CoffeeStepdefs {

    private String message;


    @When("^I order an? \"([^\"]*)\" with (\\d+) sugar$")
    public void I_order_a_with_sugar(String drinkType, int nbSugar) throws Throwable {
    }

    @Then("^the instruction generated should be \"([^\"]*)\"$")
    public void the_instruction_generated_should_be(String expectedProtocol) throws Throwable {
    }

    @When("^the message \"([^\"]*)\" is sent$")
    public void the_message_is_sent(String message) throws Throwable {
    }

    @Given("^I've inserted (\\d+)€ in the machine$")
    public void I_ve_inserted_€_in_the_machine(int amountInEuro) throws Throwable {
    }

    @Then("^the report output should be$")
    public void the_report_output_should_be(String rawReport) throws Throwable {
    }
}
