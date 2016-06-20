CFLint-SonarQube Bridge
=======================

Trigger and import CFLint code analysis to SonarQube


## Working with your repository

#### I just want to clone this repository

If you want to simply clone this empty repository then run this command in your terminal.
```sh
git clone ssh://git@stash.stepstone.com:7999/cus/cflint-sonar-bridge.git
```
#### My code is ready to be pushed
If you already have code ready to be pushed to this repository then run this in your terminal.
```sh
cd existing-project
git init
git add --all
git commit -m "Initial Commit"
git remote add origin ssh://git@stash.stepstone.com:7999/cus/cflint-sonar-bridge.git
git push -u origin master
```
#### My code is already tracked by Git
If your code is already tracked by Git then set this repository as your "origin" to push to.
```sh
cd existing-project
git remote set-url origin ssh://git@stash.stepstone.com:7999/cus/cflint-sonar-bridge.git
git push -u origin master
```