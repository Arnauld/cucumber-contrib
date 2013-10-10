Feature: Making Money

#
#  **In order to** have daily reports of what is sold and when
#
# **As a** shop keeper
#
# **I want to** track statistics of machine usage
#

  @Reporting
  Scenario: Statistics collect basic usage

    Given the following orders:
      | time     | drink     |
      | 08:05:23 | Coffee    |
      | 08:06:43 | Coffee    |
      | 08:10:23 | Coffee    |
      | 08:45:03 | Tea       |
      | 10:05:47 | Coffee    |
      | 10:05:47 | Chocolate |
    When I query for a report
    Then the report output should be
    """
    chocolate: 1
    coffee: 4
    tea: 1
    ---
    Total: 3.00€
    """

  @Reporting
  Scenario: Statistics collect no usage

    When I query for a report
    Then the report output should be
    """
    ---
    Total: 0.00€
    """