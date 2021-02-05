# DevOps

```/!\ A .bat file was created for the execution of the project (build + container) /!\```

## Environement variables

It is preferable to use <ins>environment variables</ins> to **hide** sensitive data such as a password.
The environment file may have different rights to restrict access to its data.
It is then found with data transmitted to the container without ever being visible to a "normal" user. 

```bat
docker-compose --env-file env/var.env up -d
```

## Persistent data

Docker containers **live and die**.
For containers with no data to keep, data persistence is not an issue.
However, <ins>for a database</ins>, it is important to **keep the data** even after the container has died.
When the container is launched, the folders of the container will be "synchronised" in a local folder. 

## Multistage build

The multistage build allows us to build and compile an entire package directly into a container, and copy the results of the build to the target destination.
It enables us to build and run java on any machine, without the need to install jre or jdk.

## Reverse Proxy

Using a reverse proxy allows us to not expose the API globally and to access it from a defined website.




# TP 2

## CI 

To use maven we needed to upgrade our wsl distro to wsl 2, so it could communicate with docker. After installing maven with "sudo apt install maven" the build was successful.
The POM file specifies everything needed for the project, and enables Maven to know exactly what to do when building, like which dependencies to install for example.

Unit test is related to a precise function/feature, while integration tests take the project as a whole, makes sure that everything works with everything. It can be useful when changes are done on part of the project, to ensure there's no other problem created elsewhere with that modification.

Testcontainers is a java library dedicated to JUnit tests, for testing in docker containers purspose.

## CD

Using a develop branch allows us to run the build every time we push, first of all 'cause you don't code on the master branch. And it enables us to try directly our project in the pre prod env, without the hassle of deploying the project everytime. Using a specific branch can also be used to start different tasks depending on the needs for each branch.

To hide env var we use the travis interface to store them securelly and avoid storing them directly into the yml file. If we didn't use that way of storing env variables, it could be a security issue 'cause the values would have been written in plain text, and since env var contains passwords, it would be a huge security flaw.



# TP 3

## Ansible

it's the endpoint for the spring boot service Actuator, which is designed to monitor applications (see the metrics, the logs, ...)

$basearch is related to the architecture (x86, x64, ARM, ...) the app is deployed on, it allows the playbook to deploy the right architecture on whatever machine it is deployed.
