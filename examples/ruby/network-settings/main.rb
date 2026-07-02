# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

# Default settings
first_sandbox = nightona.create
puts "Network block all: #{first_sandbox.network_block_all}"
puts "Network allow list: #{first_sandbox.network_allow_list}"

# Block all network access
second_sandbox = nightona.create(Nightona::CreateSandboxFromSnapshotParams.new(network_block_all: true))
puts "Network block all: #{second_sandbox.network_block_all}"
puts "Network allow list: #{second_sandbox.network_allow_list}"

# Explicitly allow list of network addresses
third_sandbox = nightona.create(
  Nightona::CreateSandboxFromSnapshotParams.new(network_allow_list: '192.168.1.0/16,10.0.0.0/24')
)
puts "Network block all: #{third_sandbox.network_block_all}"
puts "Network allow list: #{third_sandbox.network_allow_list}"

nightona.delete(first_sandbox)
nightona.delete(second_sandbox)
nightona.delete(third_sandbox)
