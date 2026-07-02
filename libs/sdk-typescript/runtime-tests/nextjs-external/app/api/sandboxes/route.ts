// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import { Nightona, Image } from '@nightona/sdk'

export const dynamic = 'force-dynamic'

export async function GET() {
  const image = Image.base('alpine').env({ FOO: 'bar' })
  const nightona = new Nightona()
  const r = await nightona.snapshot.list()
  return Response.json({
    imageOk: image.dockerfile.includes('FROM alpine'),
    listOk: Array.isArray(r.items),
  })
}
