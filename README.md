# Client Test

## Robotium
The tests use [robotium](http://code.google.com/p/robotium/) and run fully automated.
With robotium, it's quite easy to simulate user input and check the resulting layouts, Activities and or control elements with assertions.
It is built on top of JUnit and runs as Android JUnit Tests.

## Run
To be able to run the tests, add the original project as project dependency as follows:

![Add as 'health' as *required project*](./eonum-client-test/raw/master/required_project.png "Eclipse configuration")

It's recommended to execute the tests on an actual device rather than on a virtual device emulator, because of the faster execution.

Be also aware that robotium tests prefer pure Android virtual devices.
If you have multiple devices set up, it will start the pure Android per default, so be sure to choose manually or already have a emulator with Google APIs running.
