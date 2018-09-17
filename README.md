# Azazel
A better player tab list library for Minecraft 1.7-1.8+

### Installation

#### Option 1: Maven repository 
    *Coming soon*
#### Option 2: JAR
  1. Download the [latest release](https://github.com/bizarre/azazel/releases).
  2. Add the JAR to your project.
    + For Eclipse users, see [here](http://stackoverflow.com/questions/11033603/how-to-create-a-jar-with-external-libraries-included-in-eclipse).
    + For IntelliJ users, see [here](http://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project).

Instantiate Azazel in your onEnable:

  ```java
public void onEnable() {
      //All your other stuff
      new Azazel(this, tabadapter);
}
  
  ```
  
### Example Usage

see [ExampleTabAdapter.java](https://github.com/bizarre/Azazel/blob/master/src/main/java/com/bizarrealex/azazel/tab/example/ExampleTabAdapter.java)

#### Result
![result](https://i.gyazo.com/0d4d4ae6fb58a00f57cee614d8600727.png)
