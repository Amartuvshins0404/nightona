/*
 * Copyright Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { motion, MotionProps } from 'motion/react'
import { ComponentProps, useId } from 'react'

const wordmarkProps: MotionProps = {
  initial: {
    filter: 'blur(2px)',
    x: -4,
    opacity: 0,
  },
  animate: {
    filter: 'blur(0px)',
    x: 0,
    opacity: 1,
  },
  transition: {
    duration: 0.3,
    ease: 'easeInOut',
    delay: 0.15,
  },
}

const logoContainerProps: MotionProps = {
  initial: {
    rotate: 0,
  },
  animate: {
    rotate: -360,
  },
  transition: {
    type: 'spring',
    stiffness: 30,
    duration: 1.25,
  },
}

const logoMoonProps: MotionProps = {
  initial: { opacity: 0 },
  animate: { opacity: [0, 1] },
  transition: { duration: 0.4 },
}

type AnimatedLogoProps = ComponentProps<typeof motion.svg> & {
  animated?: boolean
}

const animationProps = (animated: boolean, props: MotionProps): MotionProps => (animated ? props : {})

export function AnimatedLogo({ animated = true, ...props }: AnimatedLogoProps) {
  const clipPathId = useId()

  return (
    <motion.svg width="266" height="60" viewBox="0 0 266 60" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
      <motion.text
        {...animationProps(animated, wordmarkProps)}
        x="60"
        y="31"
        dominantBaseline="central"
        fontFamily="'Helvetica Neue', Helvetica, Arial, sans-serif"
        fontSize="35"
        fontWeight="700"
        letterSpacing="2"
        textLength="196"
        lengthAdjust="spacingAndGlyphs"
        fill="currentColor"
      >
        NIGHTONA
      </motion.text>

      <g clipPath={`url(#${clipPathId})`}>
        <motion.g {...animationProps(animated, logoContainerProps)}>
          <motion.path
            {...animationProps(animated, logoMoonProps)}
            transform="translate(6 9) scale(1.75)"
            d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"
            fill="currentColor"
          />
        </motion.g>
      </g>
      <defs>
        <clipPath id={clipPathId}>
          <rect width="266" height="60" fill="white" />
        </clipPath>
      </defs>
    </motion.svg>
  )
}
