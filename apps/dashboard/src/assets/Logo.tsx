/*
 * Copyright 2025 Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

export function Logo(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="27" height="28" fill="currentColor" viewBox="0 0 27 28" {...props}>
      <path transform="translate(1.5 2)" d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
    </svg>
  )
}

export function LogoText() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="82" height="28" fill="currentColor" viewBox="0 0 82 28">
      <text
        x="1"
        y="14.5"
        dominantBaseline="central"
        fontFamily="'Helvetica Neue', Helvetica, Arial, sans-serif"
        fontSize="15"
        fontWeight="700"
        letterSpacing="0.5"
        textLength="80"
        lengthAdjust="spacingAndGlyphs"
      >
        NIGHTONA
      </text>
    </svg>
  )
}
