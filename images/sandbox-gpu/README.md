# Nightona Sandbox GPU Image

[Dockerfile](./Dockerfile) defines the GPU-enabled Nightona sandbox image, published as `ghcr.io/amartuvshins0404/sandbox-gpu`, for x86 GPU hosts (e.g. H100). It is derived from the upstream [daytonaio/sandbox](https://hub.docker.com/r/daytonaio/sandbox) image lineage.

It is a **superset of the default [sandbox](../sandbox) image** — it builds `FROM daytonaio/sandbox` by default (CI substitutes the freshly built `ghcr.io/amartuvshins0404/sandbox` base) and layers the GPU stack on top, so everything in the standard sandbox (Python, Node, language servers, the computer-use/VNC tooling, the default Python/agent packages, the `nightona` user) is present, plus GPU support.

## NOTE

This image is **amd64-only** — there is no arm64 variant.

## Added on top of the base image

CUDA / GPU runtime:

- CUDA 13 toolkit (`nvcc`), installed via the NVIDIA runfile
- PyTorch (CUDA 13 build) — `torch`, `torchvision`, `torchaudio`
- vLLM, with FlashInfer kernels pre-staged so first-serve cold-start stays fast
- FlashAttention (bundled with vLLM), cuDNN

GPU / ML / fine-tuning:

- accelerate
- datasets
- safetensors
- peft
- bitsandbytes
- trl
- einops
- sentencepiece
- sentence-transformers
- nvitop

Experiment tracking:

- wandb
- tensorboard

Interactive:

- jupyterlab
- ipykernel
- ipywidgets
