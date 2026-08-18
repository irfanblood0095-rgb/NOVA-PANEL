# Nova Panel — Floating UI Demo

Ei project ekta **theme/UI demo** — kono game-hack na. Purple/blue futuristic
style-e ekta draggable floating icon, ja tap korle 12-ta toggle-row soho
ekta panel open hoy. Prottekta switch shudhu nijer visual state (on/off +
ekta Toast) change kore — kono game process, memory, ba root access-er
sathe kono somporko nei.

## Ki ache

- `MainActivity.kt` — overlay permission request + service start button
- `OverlayService.kt` — draggable floating icon + expandable panel (WindowManager)
- Layouts: `activity_main.xml`, `floating_icon.xml`, `floating_menu.xml`, `item_feature_row.xml`
- Purple/blue gradient theme: `colors.xml`, `bg_panel.xml`, `bg_float_icon.xml`, ইত্যাদি

## Build kivabe korben

1. Android Studio-te "Open" diye ei folder ta select korun.
2. Gradle sync হতে দিন।
3. Run ▶ চাপুন (emulator বা device — **root লাগবে না**)।
4. App khule "Overlay Permission Grant Korun" চাপুন → system settings-e permission on korun।
5. Ferot esse "Panel Chalu Korun" চাপুন — screen-e ekta floating circle icon ashbe।
6. Icon-e tap korle panel open hobe, drag korle move hobe।

## Scope / Limitations (ইচ্ছাকৃত)

- Kono root/su access lage na
- Kono onno app-er process, memory, ba game engine-er sathe interact kore na
- Switch গুলো purely cosmetic — শুধু নিজের UI state আর একটা Toast message দেখায়
- Ei design pattern (floating icon → expandable panel) legit use-case-e o
  common — jemon accessibility tools, screen-recorder controls, ba
  productivity floating widgets

Jodi kokhono real feature (jemon actual overlay stats, screen recording
control, ba onno legitimate root-based system utility) add korte chan,
seigula alada bhabe банано jete pare — kintu kono game-memory access ba
anti-cheat bypass kora jabe na.
