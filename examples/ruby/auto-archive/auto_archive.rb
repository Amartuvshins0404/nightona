# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

# Default interval
first_sandbox = nightona.create
puts "Default auto archive interval: #{first_sandbox.auto_archive_interval}"

# Set interval to 1 hour
first_sandbox.auto_archive_interval = 60
puts "Auto archive interval: #{first_sandbox.auto_archive_interval}"

# Max interval
second_sandbox = nightona.create(Nightona::CreateSandboxFromSnapshotParams.new(auto_archive_interval: 0))
puts "Max auto archive interval: #{second_sandbox.auto_archive_interval}"

# 1 day interval
third_sandbox = nightona.create(Nightona::CreateSandboxFromSnapshotParams.new(auto_archive_interval: 24 * 60))
puts "Auto archive interval: #{third_sandbox.auto_archive_interval}"

nightona.delete(first_sandbox)
nightona.delete(second_sandbox)
nightona.delete(third_sandbox)
