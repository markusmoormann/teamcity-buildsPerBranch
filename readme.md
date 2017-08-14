# Teamcity Builds per Branch Plugin

A TeamCity plugin for running parallel builds in different branches. After installing and configuring this plugin TeamCity queues builds if there is already one running build in this branch. 

## Usage instructions
### Installing the plugin

The plugin is distributed as a ZIP file, and is available from our download page. To install:

1. Download the TeamCity Plugin ZIP file
2. Copy the ZIP file to <TeamCity Data Directory>/plugins
3. Restart Teamcity Server
4. The plugin should be listed in <TeamcityURL>/admin/admin.html?item=plugins

### Using the Plugin
To enable the Plugin go to your BuildType (or project settings for using the plugin for all buildTypes in your project) settings and add `build_per_branch_enabled = true` to your parameter list.
You need to set the limit of simultaneously running builds (on general settings page) to 0.

If you have other then `master` as your default branch you can set your default branch via `build_per_branch_default_branch` parameter
