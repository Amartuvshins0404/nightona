# Branding TODO

Status of the visual rebrand (Daytona -> Nightona wordmark: crescent moon + "NIGHTONA").

## Replaced in this pass (no action needed, listed for traceability)

- `assets/images/Nightona-logotype-black.png` / `-white.png` (4140x920, transparent) — regenerated from the new SVG wordmark; SVG sources added alongside as `Nightona-logotype-black.svg` / `-white.svg`.
- `apps/dashboard/src/assets/nightona-logo-black.png` / `-white.png` (844x880) — crescent-moon icon.
- `apps/dashboard/src/assets/nightona-full-black.png` / `-white.png` (1500x360) — full logotype.
- `apps/dashboard/src/assets/Logo.tsx` (`Logo`, `LogoText`) and `apps/dashboard/src/components/AnimatedLogo.tsx` — now render the Nightona moon + wordmark (same component APIs and viewBox proportions).
- `apps/dashboard/public/favicon.svg` (new) + `apps/dashboard/public/favicon.ico` and `apps/docs/public/favicon.ico` — night-navy rounded square with white crescent. Note: the `.ico` files contain PNG-compressed entries (16/32/48 px), which every modern browser supports; regenerate as BMP-encoded ICO only if support for very old browsers (IE < 11) is required.
- `apps/docs/src/assets/icons/nightona-logo.svg` — docs navbar logo.
- `apps/docs/public/opengraph.png` (2400x1256) and `apps/docs/public/nightona.png` (1248x628) — dark OG cards with the new wordmark.

## Remaining items needing manual attention

1. ~~**`apps/cli/auth/auth_success.html`**~~ — RESOLVED: the post-login page now renders an inline
   SVG of the Nightona wordmark (crescent + NIGHTONA) and an inline data-URI crescent favicon;
   no external fetches remain.
2. ~~**`apps/docs/src/components/ApiReference.astro`**~~ — RESOLVED: Scalar config `image` /
   `ogImage` now point at
   `https://raw.githubusercontent.com/Amartuvshins0404/nightona/main/apps/docs/public/nightona.png`
   (the rebranded 1248x628 OG card in this repo).
3. **Contact emails** — `sales@daytona.io` / `support@daytona.io` mailto links remain in
   `LoadingFallbackContent.tsx` and `UsageOverview.tsx` (no Nightona address exists yet).
   `TierUpgradeCard.tsx` and `apps/api/src/common/constants/error-messages.ts` now point at
   `https://github.com/Amartuvshins0404/nightona/issues` instead.
4. **Docs URLs** — `NIGHTONA_DOCS_URL` in `apps/dashboard/src/constants/ExternalLinks.ts` and other
   deep links still point at `https://www.daytona.io/docs` by design (upstream docs; no replacement
   domain yet). Switch them once Nightona docs are hosted.
5. **Render quality** — all PNGs were rasterized from the SVG wordmark (bold Helvetica Neue) via
   headless Chrome. Good enough for UI use; a designer may want to re-export from a proper brand file
   and convert the wordmark text to outlined paths so the SVGs render identically without system fonts.

## Explicitly untouched (not Nightona/Daytona branding)

- `apps/dashboard/src/assets/bbox-logo-dark.png` / `-light.png` — third-party "bb" partner logo.
- `ghcr.io/daytonaio` / `daytonaio/sandbox` image refs, `github.com/daytonaio` lineage links,
  `LICENSE`, `COPYRIGHT`, `NOTICE`, root `README.md` — per repo hard rules.
