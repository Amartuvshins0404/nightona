// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { app, HttpRequest, HttpResponseInit, InvocationContext } from '@azure/functions'
import { Nightona, Image } from '@nightona-co/sdk'

export async function sandboxesHandler(_req: HttpRequest, _ctx: InvocationContext): Promise<HttpResponseInit> {
  const image = Image.base('alpine').env({ FOO: 'bar' })
  const nightona = new Nightona({
    apiKey: process.env.NIGHTONA_API_KEY,
    apiUrl: process.env.NIGHTONA_API_URL,
  })
  const iter = nightona.list()
  const listOk = typeof (iter as any)[Symbol.asyncIterator] === 'function' && typeof (await iter.next()) === 'object'
  return {
    jsonBody: {
      imageOk: image.dockerfile.includes('FROM alpine'),
      listOk,
    },
  }
}

app.http('sandboxes', { methods: ['GET'], handler: sandboxesHandler })
