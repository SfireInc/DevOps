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

To use maven we needed to upgrade our wsl distro to wsl 2, so it could communicate with docker. After installing maven with "sudo apt install maven" the build was successful.
