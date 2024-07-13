FROM ubuntu:latest
LABEL authors="uberl"

ENTRYPOINT ["top", "-b"]