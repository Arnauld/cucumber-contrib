Feature: Forward Start Option, Strike Forward

  As a trader or middle office
  I want to ...


  Scenario: Something

    Given the following product information:
      | Property | Value       |
      | maturity | 2014-JUL-27 |
    When I generate the XML on 2014-JUN-05
    Then the XML should be
    """
    <product>
      <generated>2014/06/05</generated>
      <maturity>2014/07/27</maturity>
    </product>
    """