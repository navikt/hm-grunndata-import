FROM ghcr.io/navikt/baseimages/temurin:17
USER apprunner
COPY build/libs/hm-grunndata-import-all.jar ./app.jar

