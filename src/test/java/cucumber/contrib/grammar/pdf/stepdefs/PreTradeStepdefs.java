package cucumber.contrib.grammar.pdf.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.Format;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Date;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PreTradeStepdefs {
    @Given("^the following product information:")
    public void currentProduct(DataTable table) {}


    @When("^I generate the XML on (.+)$")
    public void generateXML(@Format("yyyy-MMM-dd") Date date) {
    }

    @Then("^the XML should be")
    public void assertXML(String content) {}

}
