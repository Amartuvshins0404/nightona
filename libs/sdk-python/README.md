# Nightona Python SDK

The official Python SDK for [Nightona](https://daytona.io), a secure and elastic infrastructure for running AI-generated code. Nightona provides full composable computers — [sandboxes](https://www.daytona.io/docs/en/sandboxes/) — that you can manage programmatically using the Nightona SDK.

The SDK provides an interface for sandbox management, file system operations, Git operations, language server protocol support, process and code execution, and computer use. For more information, see the [documentation](https://www.daytona.io/docs/en/python-sdk/).

## Installation

Install the package using **pip**:

```bash
pip install nightona
```

## Get API key

Generate an API key from the [Nightona Dashboard ↗](https://app.daytona.io/dashboard/keys) to authenticate SDK requests and access Nightona services. For more information, see the [API keys](https://www.daytona.io/docs/en/api-keys/) documentation.

## Configuration

Configure the SDK using [environment variables](https://www.daytona.io/docs/en/configuration/#environment-variables) or by passing a [configuration object](https://www.daytona.io/docs/en/configuration/#configuration-in-code):

- `NIGHTONA_API_KEY`: Your Nightona [API key](https://www.daytona.io/docs/en/api-keys/)
- `NIGHTONA_API_URL`: The Nightona [API URL](https://www.daytona.io/docs/en/tools/api/)
- `NIGHTONA_TARGET`: Your target [region](https://www.daytona.io/docs/en/regions/) environment (e.g. `us`, `eu`)

```python
from nightona import Nightona, NightonaConfig

# Initialize with environment variables
nightona = Nightona()

# Initialize with configuration object
config = NightonaConfig(
    api_key="YOUR_API_KEY",
    api_url="YOUR_API_URL",
    target="us"
)
```

## Create a sandbox

Create a sandbox to run your code securely in an isolated environment.

```python
from nightona import Nightona, NightonaConfig

config = NightonaConfig(api_key="YOUR_API_KEY")
nightona = Nightona(config)
sandbox = nightona.create()
response = sandbox.process.code_run('print("Hello World")')
```

## Examples and guides

Nightona provides [examples](https://www.daytona.io/docs/en/getting-started/#examples) and [guides](https://www.daytona.io/docs/en/guides/) for common sandbox operations, best practices, and a wide range of topics, from basic usage to advanced topics, showcasing various types of integrations between Nightona and other tools.

### Create a sandbox with custom resources

Create a sandbox with [custom resources](https://www.daytona.io/docs/en/sandboxes/#resources) (CPU, memory, disk).

```python
from nightona import Nightona, CreateSandboxFromImageParams, Image, Resources

nightona = Nightona()
sandbox = nightona.create(
    CreateSandboxFromImageParams(
        image=Image.debian_slim("3.12"),
        resources=Resources(cpu=2, memory=4, disk=8)
    )
)
```

### Create an ephemeral sandbox

Create an [ephemeral sandbox](https://www.daytona.io/docs/en/sandboxes/#ephemeral-sandboxes) that is automatically deleted when stopped.

```python
from nightona import Nightona, CreateSandboxFromSnapshotParams

nightona = Nightona()
sandbox = nightona.create(
    CreateSandboxFromSnapshotParams(ephemeral=True, auto_stop_interval=5)
)
```

### Create a sandbox from a snapshot

Create a sandbox from a [snapshot](https://www.daytona.io/docs/en/snapshots/).

```python
from nightona import Nightona, CreateSandboxFromSnapshotParams

nightona = Nightona()
sandbox = nightona.create(
    CreateSandboxFromSnapshotParams(
        snapshot="my-snapshot-name",
        language="python"
    )
)
```

### Execute Commands

Execute commands in the sandbox.

```python
# Execute a shell command
response = sandbox.process.exec('echo "Hello, World!"')
print(response.result)

# Run Python code
response = sandbox.process.code_run('''
x = 10
y = 20
print(f"Sum: {x + y}")
''')
print(response.result)
```

### File Operations

Upload, download, and search files in the sandbox.

```python
# Upload a file
sandbox.fs.upload_file(b'Hello, World!', 'path/to/file.txt')

# Download a file
content = sandbox.fs.download_file('path/to/file.txt')

# Search for files
matches = sandbox.fs.find_files(root_dir, 'search_pattern')
```

### Git Operations

Clone, list branches, and add files to the sandbox.

```python
# Clone a repository
sandbox.git.clone('https://github.com/example/repo', 'path/to/clone')

# List branches
branches = sandbox.git.branches('path/to/repo')

# Add files
sandbox.git.add('path/to/repo', ['file1.txt', 'file2.txt'])
```

### Language Server Protocol

Create and start a language server to get code completions, document symbols, and more.

```python
# Create and start a language server
lsp = sandbox.create_lsp_server('python', 'path/to/project')
lsp.start()

# Notify the lsp for the file
lsp.did_open('path/to/file.py')

# Get document symbols
symbols = lsp.document_symbols('path/to/file.py')

# Get completions
completions = lsp.completions('path/to/file.py', {"line": 10, "character": 15})
```

Code in [\_sync](./src/nightona/_sync/) directory shouldn't be edited directly. It should be generated from the corresponding async code in the [\_async](./src/nightona/_async/) directory using the SDK generation scripts in the [scripts](./scripts/) directory.
