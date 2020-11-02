# SAMS Data Warehouse

![logo](https://sams-project.eu/wp-content/uploads/2019/11/SAMS-DW.png)

SAMS is a project funded by the European Union within the H2020-ICT-39-2016-2017 call. 
SAMS enhances international cooperation of ICT (Information and Communication Technologies) 
and sustainable agriculture between EU and developing countries in pursuit of the EU commitment 
to the UN Sustainable Development Goal “End hunger, achieve food security and improved nutrition 
and promote sustainable agriculture”.
To get more information about the SAMS project please visit: https://sams-project.eu/

SAMS Data Warehouse (DW) is a universal system, which is able to operate with different
 data inputs and have flexible data processing algorithms. 

![architecture](https://github.com/Vitaljok/sams-dw/raw/master/front-end/src/assets/DW-concept-new.svg)

# SAMS DW Core
This software is part of SAMS DW solution, it provides data storage and processing 
services needed for [SAMS DW Web API](https://github.com/Vitaljok/sams-dw/).

DW Core is build with a concept to accept any incoming data and process it through the 
number of vaults applying desired transformation to the data.

The software consists of two modules:
* core-common - internal infrastructure and generic base classes;
* core-bees - SAMS DW specific implementation of data processing pipelines.

# Running local (development) environment
SAMS DW Core by default requires MongoDB instance to be available on 
`localhost:27017` (can be changed in a configuration).

**Core** is started by `lv.llu.science.dwh.bees.BeesDwhCore` class via IDE of your choice 
or by running 
```
./gradlew core-bees:bootRun
```

Note for IntelliJ IDEA users: install `Lombok` plugin 
and enable annotation processing in `Settings > Build, Execution, Deployment > Compiler > Annotation Processors`.

# Building artifacts
```
./gradlew build
```
Compiled artifacts are available in `core-bees/build/libs` directory.

# Deployment and hosting

It is recommended to use Docker platform for deployment of SAMS DW solution 
with all related services.

To build Docker image of SAMS DW Core run
```
./gradlew web-api:dockerImage
```

If needed private image registry can be configured in `./docker-build.gradle` file, 
and images can be pushed to it as follows
 
```
./gradlew web-api:dockerPush
```