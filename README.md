## What is this project
Group project for the ETH Zurich course Applied Information Security. Authors: Alessio Bähler, Andreas Enz and Matthias Niederberger.

## How to deploy the project for the first time
- clone this git repository
- cd SecLabProject/System/Deployment
- run *vagrant up* to start all the virtual machines. Note that this make take a while.
- see next section for tipps on how to interact with the the vagrant environment

## Vagrant cheat sheet
- Official documentation page: https://www.vagrantup.com/docs/index.html
- *vagrant up*: creates, configures and starts all guest machines in the vagrant file
- *vagrant up* [name|id]: creates, configures and starts only speficied guest machines from the vagrant file
- *vagrant halt*: TODO
- *vagrant halt [name|id]*: TODO
- *vagrant ssh [name|id]*: TODO
- *vagrant suspend [name|id]*: TODO
- *vagrant resume [name|id]*: TODO
- *vagrant destroy*: TODO
- *vagrant destroy [name|id]*: TODO
- *vagrant reload [name|id]*: TODO


## How to work with the environment
Creating each time the whole environment is costly in terms of time and resources, so to develop and test
I would advise to use *halt/up* (stop/start VM) and *suspend/resume* (hybernate/resume VM).
TODO

## Useful sources
TODO

## IP Topo
![](/System%20Description/res/basic_topo.png)