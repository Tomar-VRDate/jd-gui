# JD-GUI

JD-GUI is a standalone graphical utility that displays Java source codes of ".class" files. You can browse the
reconstructed source code with the JD-GUI for instant access to methods and fields.

![](https://raw.githubusercontent.com/java-decompiler/jd-gui/master/src/website/img/jd-gui.png)

## Forked from

- Java Decompiler projects home page: [http://java-decompiler.github.io](http://java-decompiler.github.io)
- JD-GUI source code: [https://github.com/java-decompiler/jd-gui](https://github.com/java-decompiler/jd-gui)
- QuiltFlower source code: [https://github.com/QuiltMC/quiltflower](https://github.com/QuiltMC/quiltflower)

## Changes in this fork

1. [QuiltFlower 1.7.0](https://github.com/Tomar-VRDate/quiltflower) embedded and used by default instead of JD Core.
2. Set at org.jd.gui.service.preferencespanel.ClassFileDecompilerPreferences.decompileWithQuiltflower using
   org.jd.gui.service.preferencespanel.GenericPreferencesPanelProvider and
   org.jd.gui.service.preferencespanel.Preference implementations like
   org.jd.gui.service.preferencespanel.QuiltflowerFileSaverPreferencesProvider for
   org.jd.gui.service.preferencespanel.QuiltflowerFileSaverPreferences
3. Code Reformatted with .idea/ProjectCodeStyle.xml
4. Added .idea files to repository except .idea/workspace.xml
5. Naming convention for version YYYY.MM.DD instead of 1.6.6 core 1.1.3
6. Preferences (Ctrl+Shift+P) of SourceSaver set to false so original line numbers and metadata are not generated by
   default.
7. Save All Sources (Ctrl+Alt+P) of a jar file named pkg.jar pkg-sources.jar will suggest the file name in order to
   comply with maven conventions.
8. Planned changed:
   1. Integration with other decompiler libraries using preferences at app/build.gradle
      2. [CFR](https://github.com/Tomar-VRDate/cfr) main site hosted
         at [benf.org/other/cfr](https://github.com/Tomar-VRDate/cfr)
   2. Upgrade of ANTLR services/src/main/antlr/Java.g4
      using [/grammars-v4/tree/master/java/java](https://github.com/Tomar-VRDate/grammars-v4/tree/master/java/java)

## How to build JD-GUI ?

```
> git clone https://github.com/java-decompiler/jd-gui.git
> cd jd-gui
> ./gradlew build 
```

generate :

- _"build/libs/jd-gui-x.y.z.jar"_
- _"build/libs/jd-gui-x.y.z-min.jar"_
- _"build/distributions/jd-gui-windows-x.y.z.zip"_
- _"build/distributions/jd-gui-osx-x.y.z.tar"_
- _"build/distributions/jd-gui-x.y.z.deb"_
- _"build/distributions/jd-gui-x.y.z.rpm"_

## How to launch JD-GUI ?

- Double-click on _"jd-gui-x.y.z.jar"_
- Double-click on _"jd-gui.exe"_ application from Windows
- Double-click on _"JD-GUI"_ application from Mac OSX
- Execute _"java -jar jd-gui-x.y.z.jar"_ or _"java -classpath jd-gui-x.y.z.jar org.jd.gui.App"_

## How to use JD-GUI ?

- Open a file with menu "File > Open File..."
- Open recent files with menu "File > Recent Files"
- Drag and drop files from your file explorer

## How to extend JD-GUI ?

```
> ./gradlew idea 
```

generate Idea Intellij project

```
> ./gradlew eclipse
```

generate Eclipse project

```
> java -classpath jd-gui-x.y.z.jar;myextension1.jar;myextension2.jar org.jd.gui.App
```

launch JD-GUI with your extensions

## How to uninstall JD-GUI ?

- Java: Delete "jd-gui-x.y.z.jar" and "jd-gui.cfg".
- Mac OSX: Drag and drop "JD-GUI" application into the trash.
- Windows: Delete "jd-gui.exe" and "jd-gui.cfg".

## License

Released under the [GNU GPL v3](LICENSE).