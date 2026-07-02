# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

result = nightona.snapshot.list(page: 2, limit: 10)
result.items.each do |snapshot|
  puts "#{snapshot.name} (#{snapshot.image_name})"
end
