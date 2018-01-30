
- Safe Driving Practice Simulator requires JRE/JDK 8 with minimum memory heap for JVM larger than 256MB.

- The simulator features all the minimum stated requirements, without any deprecated code.

- Currently runs on only 1280x720 resolution.

- If a '.jar' file exists, simply double click on the application to execute the program. For the application
to run, 'files' folder is necessary as it contains login details and files required by application, else the
program won't accordingly.

- There is no main method, the starting point of the program is located in
src\safedrivingpracticesimulatorfx\SafeDrivingPracticeSimulatorFX.java as this is a JavaFX project,
there is a start method which acts like the main method.

- On logging in with your userid and password, you can either proceed for demo or practice test.
You can take demo maximum 3 times. Demo will be automated to guide you regarding the running of the
real test.

Required Source files and folders

- SafeDrivingPracticeSimulatorFX.jar
- src\safedrivingpracticesimulatorfx\SafeDrivingPracticeSimulatorFX.java
- src\model\LoginModel.java
- src\model\SimulationModel.java
- src\model\Demo.java
- src\view\SimulationPane.java
- src\view\ChooserView.fxml
- src\view\LoginView.fxml
- src\view\SimulationView.fxml
- src\controller\MainController.java
- src\controller\ChooserController.java
- src\controller\SimulationController.java
- files\images\car1.png
- files\images\car2.png
- files\images\car3.png
- files\images\car4.png
- files\images\car5.png
- files\images\car6.png
- files\images\car7.png
- files\images\car8.png
- files\images\child.png
- files\images\legend.png
- files\images\roadtexture.png
- files\images\roadwork.png
- files\images\schoolzone.png
- files\images\stars.png
- files\Login.txt
