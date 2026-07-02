# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

puts 'Creating sandbox'
sandbox = nightona.create
puts 'Sandbox created'

puts 'Replacing sandbox labels'
sandbox.labels = { public: true }
puts "Sandbox labels: #{sandbox.labels}"

puts 'Stopping sandbox'
nightona.stop(sandbox)
puts "Sandbox #{sandbox.state}"

puts 'Starting sandbox'
nightona.start(sandbox)
puts "Sandbox #{sandbox.state}"

puts 'Getting existing sandbox'
sandbox = nightona.get(sandbox.id)
puts 'Retrieved existing sandbox'

response = sandbox.process.exec(command: 'echo "Hello World from exec!"', cwd: '/home/nightona', timeout: 10)
if response.exit_code == 0
  puts response.result
else
  puts "Error: #{response.exit_code} #{response.result}"
end

sandboxes = nightona.list.to_a
puts "Total sandboxes count: #{sandboxes.size}"

puts "Printing sandboxes[0] -> id: #{sandboxes.first.id} state: #{sandboxes.first.state}" unless sandboxes.empty?

puts 'Removing sandbox'
nightona.delete(sandbox)
puts 'Sandbox removed'
