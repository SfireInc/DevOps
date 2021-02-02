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
