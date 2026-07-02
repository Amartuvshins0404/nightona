FROM golang:1.26.4-alpine AS go-toolchain

FROM node:22-alpine AS build

ENV CI=true

# Build toolchain (python3/py3-setuptools/make/g++) is needed when a native
# module's prebuilt binary download fails and node-gyp falls back to compiling
# from source — it stays out of the runtime image. py3-setuptools provides the
# distutils module node-gyp needs on Python 3.12+.
RUN apk add --no-cache python3 py3-setuptools make g++

RUN npm install -g corepack && corepack enable

COPY --from=go-toolchain /usr/local/go/ /usr/local/go/

ENV PATH="/usr/local/go/bin:${PATH}"

RUN apk add --no-cache git

WORKDIR /nightona

# Yarn caching layer
COPY package.json yarn.lock .yarnrc.yml ./
RUN yarn install --immutable

# Nx config
COPY nx.json .nxignore ./

# Go dependency layer (cached unless go.mod/go.sum change)
COPY go.work go.work.sum ./
COPY apps/snapshot-manager/go.mod apps/snapshot-manager/go.sum apps/snapshot-manager/
RUN head -1 go.work > go.work.tmp && printf '\nuse (\n\t./apps/snapshot-manager\n)\n' >> go.work.tmp && mv go.work.tmp go.work

ENV NX_DAEMON=false
ENV GONOSUMDB=github.com/nightona-co/nightona

RUN cd apps/snapshot-manager && go mod download

# Go source
COPY apps/snapshot-manager/ apps/snapshot-manager/

ARG VERSION=0.0.1
RUN --mount=type=cache,target=/root/.cache/go-build \
  VERSION=$VERSION yarn nx build snapshot-manager --configuration=production --nxBail=true

FROM alpine:3.24 AS snapshot-manager

RUN apk update && apk upgrade --no-cache openssl pcre2 \
  && apk add --no-cache curl

WORKDIR /usr/local/bin

COPY --from=build /nightona/dist/apps/snapshot-manager nightona-snapshot-manager

RUN chmod +x nightona-snapshot-manager

ENTRYPOINT ["nightona-snapshot-manager"]
