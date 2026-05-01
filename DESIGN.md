---
name: MicroFin Core Narrative
colors:
  surface: '#f8f9fb'
  surface-dim: '#d9dadc'
  surface-bright: '#f8f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f5'
  surface-container: '#edeef0'
  surface-container-high: '#e7e8ea'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#40484c'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#eff1f2'
  outline: '#70787c'
  outline-variant: '#c0c8cc'
  surface-tint: '#28657a'
  primary: '#003544'
  on-primary: '#ffffff'
  primary-container: '#004d61'
  on-primary-container: '#83bdd4'
  inverse-primary: '#95cfe7'
  secondary: '#006781'
  on-secondary: '#ffffff'
  secondary-container: '#87ddff'
  on-secondary-container: '#00627b'
  tertiary: '#4c2700'
  on-tertiary: '#ffffff'
  tertiary-container: '#673c11'
  on-tertiary-container: '#e5a874'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b9eaff'
  primary-fixed-dim: '#95cfe7'
  on-primary-fixed: '#001f29'
  on-primary-fixed-variant: '#014d61'
  secondary-fixed: '#baeaff'
  secondary-fixed-dim: '#7bd2f4'
  on-secondary-fixed: '#001f29'
  on-secondary-fixed-variant: '#004d62'
  tertiary-fixed: '#ffdcc1'
  tertiary-fixed-dim: '#f9b984'
  on-tertiary-fixed: '#2e1500'
  on-tertiary-fixed-variant: '#683c11'
  background: '#f8f9fb'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  h1:
    fontFamily: Manrope
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  h2:
    fontFamily: Manrope
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  h3:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: '0'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  code:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: -0.01em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  container-max: 1440px
  gutter: 24px
---

## Brand & Style

The design system is engineered for the high-stakes environment of core banking. It prioritizes **trust, precision, and endurance**. The aesthetic follows a **Corporate / Modern** movement, blending the structural reliability of traditional finance with the fluid efficiency of modern SaaS. 

The emotional response is one of "calm authority." By avoiding pure white in favor of a soft, tinted background, the interface reduces eye strain for power users who interact with the system for hours. Every element is designed to feel substantial but not heavy, utilizing subtle depth and a refined color palette to guide the user through complex financial workflows without friction.

## Colors

This design system utilizes a monochromatic-adjacent teal palette to maintain a serious, institutional feel. 

- **Primary (#004D61):** Used for key actions, brand moments, and primary navigation states. It represents stability.
- **Background (#F0F4F5):** A soft grayish-cyan that acts as the canvas. It eliminates the "starkness" of pure white, making the interface feel integrated and premium.
- **Surface (#FFFFFF):** Reserved for cards and containers to create a clear "lift" from the background.
- **Accents:** Secondary shades like #007A99 are used for interactive elements like links and toggle states, while deeper shades provide high-contrast text and borders.

## Typography

The typography strategy leverages two high-performance fonts. **Manrope** is used for headings to provide a modern, slightly geometric character that feels premium. **Inter** is utilized for all functional text, data tables, and labels due to its exceptional legibility and neutral tone.

Numerical data, critical in banking, should always use tabular lining figures to ensure columns of currency align perfectly in tables and ledgers.

## Layout & Spacing

The design system employs a **12-column fixed-width grid** for desktop dashboards, centering the content with a maximum width of 1440px. For dense data views, a 4px baseline grid ensures vertical rhythm.

Margins are generous (24px to 40px) to prevent the interface from feeling "crowded," which is a common pitfall in banking software. Components use an 8px-step scale for padding and internal spacing to maintain consistent proportions across all screen sizes.

## Elevation & Depth

Hierarchy is established through **Ambient Shadows** and **Tonal Layering**. 

1.  **Level 0 (Background):** The soft #F0F4F5 canvas.
2.  **Level 1 (Surface):** White cards used for grouping content. These use a very subtle 1px border (#E0EAEB) and no shadow to feel "embedded."
3.  **Level 2 (Interactive):** Elements like dropdowns and modals use diffused shadows with a slight teal tint (`rgba(0, 77, 97, 0.08)`) to suggest they are floating above the workspace.

Shadows must be soft, with large blur radii and low opacity, avoiding the harsh blacks of legacy software.

## Shapes

The shape language is **Soft (0.25rem / 4px)**. This choice strikes a balance between the precision of sharp corners and the friendliness of fully rounded ones. 

- **Small Components:** Checkboxes and small buttons use a 4px radius.
- **Large Components:** Cards and modals use a 8px (rounded-lg) radius to soften the overall layout.
- **Iconography:** Icons should feature slightly rounded terminals to match the component language.

## Components

- **Buttons:** Primary buttons use the Dark Cyan Blue background with white text. Secondary buttons use a ghost style with a 1px border of the primary color.
- **Input Fields:** Use a white background with a subtle teal-gray border. Upon focus, the border thickens and transitions to Primary Cyan with a soft outer glow.
- **Data Tables:** The cornerstone of the system. Use "Zebra" striping with #F8FAFA for readability. Headers are sticky and use `label-md` typography.
- **Chips/Badges:** For status (e.g., "Pending", "Approved"), use low-saturation background tints of the status color with high-saturation text to ensure accessibility without overwhelming the eye.
- **Progress Indicators:** Linear, thin bars using a transition from light teal to the primary dark cyan.
- **Cards:** White containers with 24px internal padding. Section headers within cards should be separated by a subtle 1px divider.