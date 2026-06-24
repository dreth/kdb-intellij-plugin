# KDB+ Studio CE

> Maintained Marketplace fork of the original KDB+ Studio plugin by Andrii Borovyk: https://gitlab.com/shupakabras/kdb-intellij-plugin
>
> Fork source: https://github.com/dreth/kdb-intellij-plugin
>
> Marketplace plugin ID: `com.dreth.kdb.studio`
>
> This fork keeps original author credit and is distributed under GNU GPLv3. Third-party license texts are retained under `src/main/resources/META-INF/licenses/`.


This branch builds against JetBrains build 261.25134.203 and requires JDK 21.

To build the plugin ZIP:

```bash
JAVA_HOME=/path/to/jdk-21 ./gradlew buildPlugin
```

### Usage instruction

#### Toolbar visibility
 To control KDB+ Studio Toolbar visibility, use corresponding submenu in View menu:

 ![Menu](doc/howto_1.png)

Also note that disabling View->Toolbar will also hide the Studio Toolbar.

#### Toolbar action group
 Once you have enabled the Studio Toolbar, it will appear inside of the IDE view toolbar:

 ![Toolbar](doc/howto_2.png)

#### Connection management
 In order to execute any request, you need to setup at least one active connection.

 On <Select connection> combo choose "Edit connections..." submenu

 ![Edit connections](doc/howto_3.png)

 In this editor window, fill all required fields (name, host and port) and click "Apply" or "Ok" button.

 ![Connection editor](doc/hotwo_4.png)

 You can also click on "Test" button in order to verify connection settings.

 ![Check connection](doc/hotwo_5.png)

#### Execute query
 In open editor, type a query and push the "Run selected or line" button:

 ![Run button](doc/howto_6.png)

 Verify that the results appear:

 ![Response View](doc/howto_7.png)

#### Plugin settings

 To control actions keymap, choose File -> Settings. Find KDB Studio Plugin in Keymap section:

 ![Keymapping](doc/howto_8.png)

 To control plugin color and styling, choose File -> Settings. Find KDB+ in Editor -> Color Scheme section:

 ![Colors](doc/howto_9.png)

 To control plugin font, you may use the View -> KDB+ Studio Config -> Font Config dialog window.
