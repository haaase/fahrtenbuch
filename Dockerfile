FROM docker.io/sbtscala/scala-sbt:eclipse-temurin-24.0.1_9_1.11.2_3.7.1@sha256:ea5895a71c06a7eb1252e1e988d6863d0a7579fa60e4d6805bb8d4c59c5cf7ae
COPY . /app
RUN apt-get update && apt-get install -y npm
WORKDIR /app
RUN npm install
RUN npm run build

# start server
RUN apt-get install -y lighttpd
COPY docker/lighttpd.conf /app/lighttpd.conf
RUN mkdir -p /var/www
RUN mv dist/* /var/www
RUN lighttpd -tt -f lighttpd.conf
CMD ["lighttpd", "-D", "-f", "/app/lighttpd.conf"]
