#### Setup Mysql in Docker
1. download image
    `docker pull mysql/mysql-server:5.7.32`
2. run docker image.
```
docker run --name mysql1 -d -p 3306:3306 \
            -e MYSQL_DATABASE=booking \
            -e MYSQL_ROOT_PASSWORD=root \
            -e MYSQL_USER=booking \
            -e MYSQL_PASSWORD=booking \
            mysql/mysql-server:5.7.32
```
