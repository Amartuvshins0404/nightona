# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

# Generate unique name for the snapshot to avoid conflicts
snapshot_name = "python-example:#{Time.now.to_i}"

File.write('file_example.txt', 'Hello, World!')

# Create a Python image with common data science packages
image =
  Nightona::Image
  .debian_slim('3.12')
  .pip_install(%w[numpy pandas matplotlib scipy scikit-learn jupyter])
  .run_commands(
    'apt-get update && apt-get install -y git',
    'groupadd -r nightona && useradd -r -g nightona -m nightona',
    'mkdir -p /home/nightona/workspace'
  )
  .workdir('/home/nightona/workspace')
  .env(MY_ENV_VAR: 'My Environment Variable')
  .add_local_file('file_example.txt', '/home/nightona/workspace/file_example.txt')

nightona.snapshot.create(
  Nightona::CreateSnapshotParams.new(
    name: snapshot_name,
    image:,
    resources: Nightona::Resources.new(cpu: 1, memory: 1, disk: 3)
  ),
  on_logs: proc { |chunk| puts chunk }
)

# Create first sandbox using the pre-built image
sandbox = nightona.create(Nightona::CreateSandboxFromSnapshotParams.new(snapshot: snapshot_name))

# Verify the first sandbox environment
response = sandbox.process.exec(command: 'python --version && pip list')
puts "Python environment: #{response.result}"

# Verify the file was added to the image
response = sandbox.process.exec(command: 'cat file_example.txt')
puts "File content: #{response.result}"

# Create sandbox with the dynamic image
dynamic_image =
  Nightona::Image
  .debian_slim('3.11')
  .pip_install(%w[pytest pytest-cov black isort mypy ruff])
  .run_commands('apt-get update && apt-get install -y git', 'mkdir -p /home/nightona/project')
  .workdir('/home/nightona/project')
  .env(ENV_VAR: 'My Environment Variable')

other_sandbox = nightona.create(
  Nightona::CreateSandboxFromImageParams.new(image: dynamic_image),
  on_snapshot_create_logs: proc { |chunk| puts chunk }
)

# Verify the other sandbox environment
response = other_sandbox.process.exec(command: "pip list | grep -E 'pytest|black|isort|mypy|ruff'")
puts "Development tools: #{response.result}"

# Cleanup
File.delete('file_example.txt') if File.exist?('file_example.txt')
nightona.delete(sandbox)
nightona.delete(other_sandbox)
