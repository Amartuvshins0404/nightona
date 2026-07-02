// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { Nightona, Image } from '@nightona/sdk'

export default {
  async fetch(_req: Request, env: any) {
    const image = Image.base('alpine').env({ FOO: 'bar' })
    const nightona = new Nightona({
      apiKey: env.NIGHTONA_API_KEY,
      apiUrl: env.NIGHTONA_API_URL,
    })
    const iter = nightona.list()
    const listOk = typeof (iter as any)[Symbol.asyncIterator] === 'function' && typeof (await iter.next()) === 'object'
    return Response.json({
      imageOk: image.dockerfile.includes('FROM alpine'),
      listOk,
    })
  },
}
