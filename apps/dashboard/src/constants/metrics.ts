/*
 * Copyright Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

export const METRIC_DISPLAY_NAMES: Record<string, string> = {
  'nightona.sandbox.cpu.utilization': 'CPU Usage (cores)',
  'nightona.sandbox.cpu.limit': 'CPU Limit',
  'nightona.sandbox.memory.utilization': 'Memory Utilization',
  'nightona.sandbox.memory.usage': 'Memory Usage',
  'nightona.sandbox.memory.limit': 'Memory Limit',
  'nightona.sandbox.filesystem.utilization': 'Disk Utilization',
  'nightona.sandbox.filesystem.usage': 'Disk Usage',
  'nightona.sandbox.filesystem.total': 'Disk Total',
  'nightona.sandbox.filesystem.available': 'Disk Available',
  'system.memory.utilization': 'System Memory Utilization',
}

export function getMetricDisplayName(metricName: string): string {
  return METRIC_DISPLAY_NAMES[metricName] ?? metricName.replace(/^nightona\.sandbox\./, '')
}
