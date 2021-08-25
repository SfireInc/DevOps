# DevOps

```/!\ A .bat file was created for the execution of the project (build + container) /!\```

## Get docker image

```sh
docker pull [image name]:[image version]
```

Or directly with ```docker run```. Docker runs through the local images. If the image does not exist locally, it will fetch it from a remote directory, by default [docker hub](https://hub.docker.com/)

## Creation of a **DockerFile**

A dockerfile allows you to specify the image to use and to modify it.

```dockerfile
FROM httpd:2.4.46

COPY ./httpd/html/ /usr/local/apache2/htdocs/
COPY ./httpd/httpd.conf /usr/local/apache2/conf/httpd.conf
```

In our case we use the ```httpd``` image in version ```2.4.46```. Then we copy files in the image to modify the httpd configuration and add the desired web page. A new image is created with the modifications made. 

To execute this dockerfile, we use: 

```sh
docker build -t [repository]/[image name]:[image version] [path to the dockerfile]
```

## Environement variables

It is preferable to use <ins>environment variables</ins> to **hide** sensitive data such as a password.
The environment file may have different rights to restrict access to its data.
It is then found with data transmitted to the container without ever being visible to a "normal" user.

In this <ins>environment file</ins> we have the password useful to the docker-compose as well as the version of the images used.
It is better to specify the version of the image used to control the execution environment and reduce the occurrence of errors. 

```sh
docker-compose --env-file [env file] up -d
```

Or if you want to pass only some variable (Only for docker run):

```sh
docker -e "POSTGRES_VERSION=11.6-alpine" -e "DB_PASSWORD=1234" [image name]
```

## docker image with initialization

```yml
db: 
  image: "postgres:${POSTGRES_VERSION}"
  volumes:
  - "./bdd/sql_file:/docker-entrypoint-initdb.d/"
```

The *Postgres* image allows us to create a database when launching the container.
Above, we notice that we copy files, in this case **sql files**, in the folder ```/docker-entrypoint-initdb.d/```. The files present in this folder at the launch of the docker will be executed.

## Persistent data

Docker containers **live and die**.
For containers with no data to keep, data persistence is not an issue.
However, <ins>for a database</ins>, it is important to **keep the data** even after the container has died.
When the container is launched, the folders of the container will be "synchronised" in a local folder. 

```yml
db: 
    image: "postgres:${POSTGRES_VERSION}"
    volumes:
    - "./bdd/persistent_volume:/var/lib/postgresql/data"
    - "./bdd/sql_file:/docker-entrypoint-initdb.d/"
```

In the example above, we mount ```/var/lib/postgresql/data``` on a folder in the current directory. At each launch of the container, this mount point will be done. So we have a persistence of our data entered in database.

## Multistage build

The multistage build allows us to build and compile an entire package directly into a container, and copy the results of the build to the target destination.
It enables us to build and run java on any machine, without the need to install jre or jdk.

For this project, we use multi-stage to build the java application and send the generated *.jar* in an ```openjdk``` image.

```dockerfile
# Build java app
FROM maven:3.6.3-jdk-11 AS myapp-build
ENV MYAPP_HOME /opt/myapp
WORKDIR $MYAPP_HOME
COPY ./simple-api/pom.xml .
COPY ./simple-api/src ./src
RUN mvn package -DskipTests

# Copy .jar in openjdk image
FROM openjdk:11-jre
ENV MYAPP_HOME /opt/myapp
WORKDIR $MYAPP_HOME
COPY --from=myapp-build $MYAPP_HOME/target/*.jar $MYAPP_HOME/myapp.jar
ENTRYPOINT java -jar myapp.jar
```

## Reverse Proxy

Using a reverse proxy allows us to not expose the API globally and to access it from a defined website.

## Docker-compose

1. Networks

    ```yml
    networks:
      bdd:
    ```

    Creation of an internal docker network.

2. Restart

    ```yml
    restart: "always"
    ```

    Specifies the behavior in case of a container crash.

3. Volumes

    ```yml
    volumes:
    - "./bdd/persistent_volume:/var/lib/postgresql/data"
    - "./bdd/sql_file:/docker-entrypoint-initdb.d/"
    ```

    Specifies the mounting points of the container.

4. Environement

    ```yml
    environment:
    - "POSTGRES_DB=db"
    - "POSTGRES_USER=toudard"
    - "POSTGRES_PASSWORD=${DB_PASSWORD}"
    ```

    Specifies the environment variables of the container.

5. Build

    ```yml
    build:
      context: .
      dockerfile: java.dockerfile
    ```

    Allows to build a dockerfile directly in a dockercompose file.

Here is the final docker-compose

```yml
version: '3.1'

networks:
  bdd:

services:
  db: 
    image: "postgres:${POSTGRES_VERSION}"
    container_name: "postgres_DevOps"
    restart: "always"
    volumes:
    - "./bdd/persistent_volume:/var/lib/postgresql/data"
    - "./bdd/sql_file:/docker-entrypoint-initdb.d/"
    environment:
    - "POSTGRES_DB=db"
    - "POSTGRES_USER=toudard"
    - "POSTGRES_PASSWORD=${DB_PASSWORD}"
    networks:
    - bdd
  
  java:
    build:
      context: .
      dockerfile: java.dockerfile
    image: "java-devops"
    container_name: "java_DevOps"
    restart: "always"
    environment:
    - "SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}"
    networks: 
    - bdd
  
  httpd:
    build:
      context: .
      dockerfile: httpd.dockerfile
    image: "httpd-devops"
    container_name: "httpd_DevOps"
    restart: "always"
    ports:
    - "80:80"
    networks: 
    - bdd
```

We execute this docker-compose using :

```sh
docker-compose --env-file env/var.env up -d
```

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

1. Below is our playbook

    ```yml
    - name: "Deployment DevOps Server"
      hosts: all
      become: yes
      gather_facts: no

      tasks:
      - name: "Installation Docker"
        include_role:
          name: install_docker
      
      - name: "Create docker network"
        include_role:
          name: create_network
      
      - name: "Launch database"
        include_role:
          name: launch_database
      
      - name: "Launch backend"
        include_role:
          name: launch_backend
      
      - name: "Launch frontend"
        include_role:
          name: launch_frontend
    ```

    

