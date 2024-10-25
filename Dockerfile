FROM gcr.io/distroless/java17-debian12:nonroot
ENV TZ="Europe/Oslo"
EXPOSE 8080
COPY build/libs/hm-grunndata-import-all.jar ./app.jar
CMD ["-jar", "/app.jar"]