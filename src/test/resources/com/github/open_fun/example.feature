Feature: Example Feature
  Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...

  Scenario: Simple scenario primer
    Given some state prepared
    When I do a specific action
    Then everything is allright

  Scenario Outline: Some outline scenario <param>
    Given lorem ipsum
    When process <param> iteration
    Examples: These examples are auto generated
    sadf asdfa sdf
    asdf asdf asdf
    asd fa sdf asdf
    as asdf  sd fasd
    fassadfa sdf asd fasd fasd
      | param | comments        |
      | 1     | lol             |
      | 2     | amazing         |
      | 3     | looks like docs |