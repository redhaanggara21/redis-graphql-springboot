FROM openjdk:17-jdk-oracle AS BUILD_IMAGE
RUN microdnf install findutils
VOLUME /tmp

ENV APP_HOME=/usr/dev/graphql/

WORKDIR $APP_HOME

COPY build.gradle gradlew gradlew.bat ${APP_HOME}/
COPY gradle ${APP_HOME}gradle/

RUN chmod +x gradlew

# download dependencies
RUN ./gradlew dependencies
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test

FROM openjdk:17-jdk-oracle
WORKDIR /usr/analytic/
COPY --from=BUILD_IMAGE /usr/dev/graphql/build/libs/graphql-server*.jar ./graphql-server.jar

EXPOSE 4000

CMD ["java","-Dspring.profiles.active=env","-Dlog4j2.formatMsgNoLookups=true","-jar","graphql-server.jar"]
