------------------------------------------------------------------
Safe Driving Practice Simulator v1.0
------------------------------------------------------------------

- Safe Driving Practice Simulator requires JRE/JDK 8.

- The simulator features all the minimum stated requirements,
without any deprecated code.

- If a '.jar' file exists, simply double click on the application
to execute the program, else execute from '.class' file where the
'main' function exists in 'SafeDrivingPracticeSimulator.class'.

- On clicking the start button, the scenario will commence and
execute for 300 seconds.

- On approaching of hazard, a 3 second timer will start after
clicking the OK in dialog box, and within 3 seconds, the car
should apply brake for the score to increment,else it is marked
as skipped.

- A tree structure for each car is implemented which displays
the clicked or skipped status at the time of hazard.

- At the end of 300 seconds, a table which shows comparison
between each car is displayed, along with a calculated response
which states the car in need for control.

- An option to save the recorded data to the file is implemented
too.

- The program does not contain any code from any sources, but
only references which are implemented differently in the software.
Those files are : JavaFXView.java and ColorSelector.java

------------------------------------------------------------------
Known bugs

- This is a bug in Swing API. On clicking on any option in menu,
the option seems to be pressed, and moving the mouse cursor in this
state to other option will lead to execution of the other option.

------------------------------------------------------------------
Required Source files and folders

- src\fileUtils\JTreeSave.java
- src\javaFXUtils\FXStartTimer.java
- src\javaFXUtils\JavaFXController.java
- src\javaFXUtils\JavaFXModel.java
- src\javaFXUtils\javaFXView.java
- src\mainPackage\SafeDrivingPracticeSimulator.java
- src\preRunConditions\ColorSelector.java
- src\preRunConditions\TermsAndConditions.java
- src\swingUtils\SwingController.java
- src\swingUtils\SwingView.java

- files\readme.txt
- files\termsandconditions.txt
- files\images\car1.png
- files\images\car2.png
- files\images\car3.png
- files\images\roadtexture.png