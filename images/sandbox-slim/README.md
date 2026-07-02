# Nightona Sandbox Slim Image

[Dockerfile](./Dockerfile) defines the slim Nightona sandbox image, published as `ghcr.io/amartuvshins0404/sandbox` with `-slim` tag suffixes (e.g. `latest-slim`) and used as the default snapshot in self-hosted environments. It is derived from the upstream [daytonaio/sandbox](https://hub.docker.com/r/daytonaio/sandbox) image lineage.

The slim sandbox image contains Python, Node and some popular dependencies including:

- pipx
- uv
- python-lsp-server
- numpy
- pandas
- matplotlib

- ts-node
- typescript
- typescript-language-server

## NOTE

The slim image does not contain dependencies necessary for Nightona's VNC functionality.
Please use the base image for that.
