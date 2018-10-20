# KDB+ Studio plugin for IntelliJ IDEA

### Usage instruction

#### Toolbar visibility 
 To control KDB+ Studio Toolbar visibility, use corresponding submenu in View menu:
 
 ![Menu](doc/howto_1.png) 
 
 Also note, disabling View->Toolbar will also hide the Studio Toolbar.

#### Toolbar action group   
 Once you have enabled the Studio Toolbar, it would appear inside of the IDE view toolbar:
 
 ![Toolbar](doc/howto_2.png)

#### Connection management
 In order to execute any request, you need to setup at least one active connection.
 
 On <Select connection> combo choose "Edit connections..." submenu
 
 ![Edit connections](doc/howto_3.png)
 
 In opened editor window, fill all required fields (name, host and port) and click "Apply" or "Ok" button.
 
 ![Connection editor](doc/hotwo_4.png)
 
 You could also click on "Test" button, in order to verify connection settings.
 
 ![Check connection](doc/hotwo_5.png)
 
#### Execute query
 In opened editor, type a query and push the "Run selected or line" button:
 
 ![Run button](doc/howto_6.png)
 
 Verify that the results appear:
 
 ![Response View](doc/howto_7.png)
 
#### Plugin settings
 
 To control actions keymap, choose File -> Settings. Find KDB Studio Plugin in Keymap section:
 
 ![Keymapping](doc/howto_8.png)
 
 To control plugin color and styling, choose File -> Settings. Find KDB+ in Editor -> Color Scheme section:
 
 ![Colors](doc/howto_9.png)
 
 To control plugin font, you may use the View -> KDB+ Studio Config -> Font Config dialog window.
   