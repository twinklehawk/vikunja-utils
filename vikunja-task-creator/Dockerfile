FROM gcr.io/distroless/java21-debian12:nonroot@sha256:7e37784d94dccbf5ccb195c73b295f5ad00cd266512dfbac12eb9c3c28f8077d

LABEL org.opencontainers.image.title=vikunja-task-creator
LABEL org.opencontainers.image.url=https://github.com/twinklehawk/vikunja-task-creator
LABEL org.opencontainers.image.source=https://github.com/twinklehawk/vikunja-task-creator
LABEL org.opencontainers.image.description="Vikunja Task Creator"
LABEL org.opencontainers.image.licenses=MIT

COPY build/libs /app
WORKDIR /app

CMD ["vikunja-task-creator.jar"]
