# Plot configuration

In order to customize chart look and feel, you should create a configuration file.

You can open chart configuration window from the chart window by selecting last item in the available configurations list:

![Chart](doc/plot_1.png)

In this window, you are able to manage all existing configurations: apply default config for each chart type, remove configuration or add a new one:

![Configurations](doc/plot_2.png)     
 
To add a new configuration, you should select JSON file which corresponds to the JSON schema available in [project sources](https://gitlab.com/shupakabras/kdb-intellij-plugin/raw/master/resources/plot-schema.json). There are several sample configurations available in the [same folder](https://gitlab.com/shupakabras/kdb-intellij-plugin/tree/master/resources): [blue-eagle-lines.json](https://gitlab.com/shupakabras/kdb-intellij-plugin/raw/master/resources/blue-eagle-lines.json), [blue-eagle-scatter.json](https://gitlab.com/shupakabras/kdb-intellij-plugin/raw/master/resources/blue-eagle-scatter.json) and [first-class-lines.json](https://gitlab.com/shupakabras/kdb-intellij-plugin/raw/master/resources/first-class-lines.json).
 
![Select path](doc/plot_3.png)

## Configure IntelliJ IDEA for JSON schema validation. 

You can also configure IDEA to validate JSON file through the JSON schema.

First, download the [latest schema](https://gitlab.com/shupakabras/kdb-intellij-plugin/raw/master/resources/plot-schema.json) from sources repo, and save it somewhere under IDEA project root.

Now open Settings window, and search for Languages & Frameworks -> Schemas and DTDs -> JSON Schema.

![JSON Schema](doc/plot_4.png)

Click on the "Plus" icon and select the plot-schema.json file.

Now you are able to add files which should be validated against the schema. So you can specify a single file, or a directory: 

![JSON files set](doc/plot_7.png)

And now, all your files under the specified directory will be validated be IDEA. So you can have inline suggestions:

![Suggestion](doc/plot_5.png)

Or validation warnings: 

![Warning](doc/plot_6.png)
