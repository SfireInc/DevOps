FROM openjdk:11
COPY Main.java /usr/src
RUN javac /usr/src/Main.java

FROM openjdk:11-jre
COPY --from=0 /usr/src/Main.class .
ENTRYPOINT [ "java", "Main" ]