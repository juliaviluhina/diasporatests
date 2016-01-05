This version is added as a result of using following approach for test which is used in branch weak-checks-and-ensure-mix:

- usage unique data for tests and weak checks for it
    - as a result - using clearing data periodically
    - as a result - stream is bigger than it can be
- usage ensure for 
    - entities for which is obligatory needed  
        - setup relations
        - setup post properties
    - entities for which is not obligatory needed according to this approach (ensurePost in test class BasicOperationsTest)
        - usage this method is started as a attempt to accelerate test (enough successful)
    - signing in
        - tests can work in two mode (system parameter signingInMode=separate for usage separate windows for user's accounts)
            - all accounts open and close consecutive in one window (test time is spent on signing in and logout)
            - each account is opened in separate window and tests are switched among this windows (test time is saved 
            at the cost of single signing in, but this mode needs more resources)

This version was developed in smart-ensure branch and merged with master branch.
 
In version is used smart ensure methods with no cleaning and no unique test data management 
because of its expected better time results 
     