// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { json } from '@remix-run/node'
import { Nightona, Image } from '@nightona/sdk'

export async function loader() {
  const image = Image.base('alpine').env({ FOO: 'bar' })
  const nightona = new Nightona()
  const iter = nightona.list()
  const listOk = typeof (iter as any)[Symbol.asyncIterator] === 'function' && typeof (await iter.next()) === 'object'
  return json({
    imageOk: image.dockerfile.includes('FROM alpine'),
    listOk,
  })
}
