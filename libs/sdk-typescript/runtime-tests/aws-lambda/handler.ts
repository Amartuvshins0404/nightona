// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { Nightona, Image } from '@nightona-co/sdk'

export const handler = async () => {
  const image = Image.base('alpine').env({ FOO: 'bar' })
  const nightona = new Nightona()
  const iter = nightona.list()
  const listOk = typeof (iter as any)[Symbol.asyncIterator] === 'function' && typeof (await iter.next()) === 'object'
  return {
    statusCode: 200,
    body: JSON.stringify({
      imageOk: image.dockerfile.includes('FROM alpine'),
      listOk,
    }),
  }
}
