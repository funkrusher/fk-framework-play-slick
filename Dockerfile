FROM eclipse-temurin:11-alpine

WORKDIR /app

RUN apk add --update --no-cache zip curl bash && apk upgrade

COPY dist/target/universal/dist.zip .
RUN unzip dist.zip -d dist && mv -v dist/*/* ./ && rm -r dist dist.zip

RUN adduser -D -S -H fk
RUN chown -R fk /app/logs

USER fk

EXPOSE 9000 3333 3334