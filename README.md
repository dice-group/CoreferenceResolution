CoreferenceResolution
=====================

Cross-Document Coreference Resolution using Latent Features

Local Maven Repository

Hey guys! This short paragraph is not about the project itself. But Ricardo told me that I should add a short explanation about the usage of the local jar files inside the lib folder. Since we are using maven and I wanted to create a runable jar file, I had to change the handling of these jar files. you can take a look at this post http://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them but note that the first solution won't be able to create a runable jar file. Only the second solution works. Therefore, I have created a small local maven repository inside the projects lib folder and installed the jars into this repository. That means tha inside of the repositories subfolder, you will find the jar files (named with some groupIds, artifactIds and versions that I have chosen freely). The repository is referenced inside the projects pom file. With this simple approach it is possible to handle the local jar files from the lib folder as normal maven dependencies making the handling of these jars much easier.
