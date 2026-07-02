# frozen_string_literal: true

require 'nightona'

nightona = Nightona::Nightona.new

nightona.list(Nightona::ListSandboxesQuery.new(
               limit: 10,
               labels: { 'env' => 'dev' },
               states: [Nightona::SandboxState::STARTED],
               sort: Nightona::SandboxListSortField::CREATED_AT,
               order: Nightona::SandboxListSortDirection::DESC
             )).each do |sandbox|
  puts sandbox.id
end
