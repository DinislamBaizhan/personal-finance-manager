FROM adoptopenjdk:17-jre-hotspot
COPY target/*.jar Auth-server.jar

ENV SPRING_DATASOURCE_URL jdbc:postgresql://dpg-chbq1d2k728tp9fncji0-a.frankfurt-postgres.render.com:5432/auth_db_0lin
ENV SPRING_DATASOURCE_USERNAME auth_db_0lin_user
ENV SPRING_DATASOURCE_PASSWORD RfPi9t8ZVs00J4I9TB7mODjFeYXaTkDN

ENTRYPOINT ["java","-jar","Auth-server.jar"]