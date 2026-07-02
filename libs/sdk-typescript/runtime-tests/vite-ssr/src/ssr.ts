// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { Nightona, Image } from '@nightona/sdk'

export async function run() {
  const image = Image.base('alpine').env({ FOO: 'bar' })
  if (!image.dockerfile.includes('FROM alpine')) throw new Error('Image.base failed')

  const nightona = new Nightona()
  const iter = nightona.list()
  if (typeof (iter as any)[Symbol.asyncIterator] !== 'function') {
    throw new Error('list() did not return an async iterator')
  }
  const first = await iter.next()
  if (typeof first !== 'object' || !('done' in first)) {
    throw new Error('list() iterator did not yield a valid result')
  }
  return 'PASS'
}
