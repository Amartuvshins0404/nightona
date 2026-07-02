# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

RSpec.describe 'Nightona common models' do
  it 'stores file upload source and destination' do
    upload = Nightona::FileUpload.new('hello', '/tmp/hello.txt')

    expect(upload.source).to eq('hello')
    expect(upload.destination).to eq('/tmp/hello.txt')
  end

  it 'stores execute response attributes' do
    artifacts = Nightona::ExecutionArtifacts.new('stdout', ['chart'])
    response = Nightona::ExecuteResponse.new(exit_code: 0, result: 'stdout', artifacts: artifacts,
                                             additional_properties: { ok: true })

    expect(response.exit_code).to eq(0)
    expect(response.result).to eq('stdout')
    expect(response.artifacts).to eq(artifacts)
    expect(response.additional_properties).to eq({ ok: true })
  end

  it 'defaults execution artifacts to empty stdout and charts' do
    artifacts = Nightona::ExecutionArtifacts.new

    expect(artifacts.stdout).to eq('')
    expect(artifacts.charts).to eq([])
  end

  it 'stores code run params' do
    params = Nightona::CodeRunParams.new(argv: ['-m', 'app'], env: { 'DEBUG' => '1' })

    expect(params.argv).to eq(['-m', 'app'])
    expect(params.env).to eq({ 'DEBUG' => '1' })
  end

  it 'stores session execute request flags' do
    req = Nightona::SessionExecuteRequest.new(command: 'bundle exec rspec', run_async: true, suppress_input_echo: true)

    expect(req.command).to eq('bundle exec rspec')
    expect(req.run_async).to be(true)
    expect(req.suppress_input_echo).to be(true)
  end

  it 'provides defaults for session execute response' do
    response = Nightona::SessionExecuteResponse.new

    expect(response.cmd_id).to be_nil
    expect(response.additional_properties).to eq({})
  end

  it 'stores session command logs response fields' do
    response = Nightona::SessionCommandLogsResponse.new(output: 'all', stdout: 'out', stderr: 'err')

    expect(response.output).to eq('all')
    expect(response.stdout).to eq('out')
    expect(response.stderr).to eq('err')
  end

  it 'stores output messages and execution errors' do
    msg = Nightona::OutputMessage.new(output: 'hello')
    error = Nightona::ExecutionError.new(name: 'ValueError', value: 'bad', traceback: 'trace')

    expect(msg.output).to eq('hello')
    expect(error.name).to eq('ValueError')
    expect(error.value).to eq('bad')
    expect(error.traceback).to eq('trace')
  end

  it 'stores execution results' do
    result = Nightona::ExecutionResult.new(stdout: 'out', stderr: 'err', error: :boom)

    expect(result.stdout).to eq('out')
    expect(result.stderr).to eq('err')
    expect(result.error).to eq(:boom)
  end

  it 'stores resource values and compacts nils' do
    resources = Nightona::Resources.new(cpu: 2, memory: 4)

    expect(resources.to_h).to eq(cpu: 2, memory: 4)
  end

  it 'stores paginated resources' do
    paginated = Nightona::PaginatedResource.new(items: [1, 2], page: 2, total: 5, total_pages: 3)

    expect(paginated.items).to eq([1, 2])
    expect(paginated.page).to eq(2)
    expect(paginated.total).to eq(5)
    expect(paginated.total_pages).to eq(3)
  end

  it 'stores git commit response sha' do
    response = Nightona::GitCommitResponse.new(sha: 'abc123')

    expect(response.sha).to eq('abc123')
  end
end
